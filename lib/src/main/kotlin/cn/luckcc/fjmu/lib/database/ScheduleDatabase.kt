@file:Suppress("NonAsciiCharacters", "NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.database

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.regexp
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import cn.luckcc.fjmu.lib.proto.CETResult
import cn.luckcc.fjmu.lib.utils.mappingData
import zx.dkk.data.encode
import cn.luckcc.fjmu.lib.database.course.*
import cn.luckcc.fjmu.lib.database.course.Classroom
import cn.luckcc.fjmu.lib.database.course.Clazz
import cn.luckcc.fjmu.lib.database.course.Course
import cn.luckcc.fjmu.lib.database.course.Teacher
import cn.luckcc.fjmu.lib.database.course.TeachingClass
import cn.luckcc.fjmu.lib.database.course.Timeline
import cn.luckcc.fjmu.lib.proto.course.*
import zx.dkk.utils.time.epochDay
import java.io.Closeable
import java.nio.file.Path
import java.time.DayOfWeek
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.absolutePathString
import kotlin.math.max

class ScheduleDatabase(storePath: Path, threadCount: Int = 1) : Closeable {
    @OptIn(DelicateCoroutinesApi::class)
    private val context = if (threadCount > 1) {
        newFixedThreadPoolContext(threadCount, "ScheduleDbExecutor")
    } else {
        newSingleThreadContext("ScheduleDbExecutor")
    }
    private val transactionLock=Any()
    private val db by lazy {
        runBlocking(context) {
            Database.connect(
                url = "jdbc:h2:${
                    storePath.absolutePathString().removeSuffix(".mv.db")
                };AUTO_SERVER=TRUE",
                driver = "org.h2.Driver",
                user = "schedule",
                password = "s_C:.h2e@du:le"
            ).apply {
                transaction(this) {
                    SchemaUtils.create(
                        Classroom,
                        Clazz,
                        Course,
                        Teacher,
                        TeachingClass,
                        Bulletin,
                        BulletinAttachment,
                        Credit,
                        CreditOverview,
                        Message,
                        Rank,
                        BookRequest,
                        CetResult,
                        Timeline,
                        SemesterTable,
                    )
                }
            }
        }
    }

    internal suspend inline fun <T, TABLE : Table> startSuspendTransaction(
        table: TABLE, context: CoroutineContext? = this.context, crossinline block: suspend TABLE.() -> T
    ) = startSuspendTransaction(context) {
        table.block()
    }

    internal suspend inline fun <T> startSuspendTransaction(
        context: CoroutineContext? = this.context, noinline block: suspend Transaction.() -> T
    )= newSuspendedTransaction(context,db, statement = block)

    suspend fun addBookRequests(
        username: String,
        requests: List<cn.luckcc.fjmu.lib.proto.BookRequest>,
        onUpdate: ((List<cn.luckcc.fjmu.lib.proto.BookRequest>) -> Unit)? = null
    ) = startSuspendTransaction(BookRequest) {

        if (requests.isEmpty())
            return@startSuspendTransaction
        onUpdate?.invoke(selectNewData(username, bookWithISBN, requests) { it.bookWithISBN })
        batchUpsert(requests) { s, req ->
            s[this.username] = username
            s[学年] = req.学年
            s[学期] = req.学期
            s[teacher] = req.teacher
            s[courseNameWithId] = req.courseNameWithId
            s[bookWithISBN] = req.bookWithISBN
        }
    }

    suspend fun getTimelines(username: String) = startSuspendTransaction(Timeline) {
        select(username(username)).singleEntity() ?: 旗山校区时间表
    }

    suspend fun setTimelines(username: String, timelines: List<cn.luckcc.fjmu.lib.proto.course.Timeline>) =
        startSuspendTransaction(Timeline) {
            upsert {
                it[this.username] = username
                it[data] = encode(timelines)
            }
        }

    suspend fun getBookRequests(
        username: String, semesterPair: ISemesterPair
    ) = startSuspendTransaction(BookRequest) {
        select(op(username, semesterPair.schoolYear, semesterPair.semester)).toEntities()
    }

    suspend fun getBookRequests(username: String) = startSuspendTransaction(BookRequest) {
        select(username(username)).toEntities()
    }

    suspend fun addCetResults(
        username: String, results: List<CETResult>, onUpdate: ((List<CETResult>) -> Unit)? = null
    ) = startSuspendTransaction(CetResult) {
        if (results.isEmpty())
            return@startSuspendTransaction
        onUpdate?.invoke(selectNewData(username, 学年, 学期, results) { it.学年 to it.学期 })
        batchUpsert(results) { s, result ->
            s[this.username] = username
            s[学年] = result.学年
            s[学期] = result.学期
            s[考试名称] = result.考试名称
            s[成绩] = result.成绩
            s[听力成绩] = result.听力成绩
            s[写作成绩] = result.写作成绩
            s[阅读成绩] = result.阅读成绩
            s[姓名] = result.姓名
        }
    }

    suspend fun getCetResults(username: String) = startSuspendTransaction(CetResult) {
        select(username(username)).toEntities()
    }

    suspend fun addCredits(
        username: String,
        credits: List<cn.luckcc.fjmu.lib.proto.Credit>,
        onUpdate: ((changes: List<Pair<cn.luckcc.fjmu.lib.proto.Credit?, cn.luckcc.fjmu.lib.proto.Credit?>>) -> Unit)? = null
    ) = startSuspendTransaction(Credit) {
        if (credits.isEmpty())
            return@startSuspendTransaction
        if (onUpdate != null) {
            val olds = getCredits(username)
            val courseIds = (credits.map { it.课程ID } + olds.map { it.课程ID }).distinct()
            val changes = arrayListOf<Pair<cn.luckcc.fjmu.lib.proto.Credit?, cn.luckcc.fjmu.lib.proto.Credit?>>()
            for (courseId in courseIds) {
                val old = olds.find { it.课程ID == courseId }
                val new = credits.find { it.课程ID == courseId }
                if (old == null && new == null) continue
                if (old == null || new == null) {
                    changes.add(old to new)
                    continue
                }
                if (old.绩点 != new.绩点 || old.平时成绩 != new.平时成绩 || old.期末成绩 != new.期末成绩 || old.学分 != new.学分 || old.成绩 != new.成绩 || old.最终成绩 != new.最终成绩) {
                    changes.add(old to new)
                }
            }
            onUpdate(changes)
        }
        batchUpsert(credits) { s, credit ->
            s[this.username] = username
            s[课程名称] = credit.课程名称
            s[课程ID] = credit.课程ID
            s[课程归属] = credit.课程归属
            s[课程性质] = credit.课程性质
            s[类型] = credit.类型
            s[绩点] = credit.绩点
            s[平时成绩] = credit.平时成绩
            s[期末成绩] = credit.期末成绩
            s[学分] = credit.学分
            s[学期] = credit.学期
            s[成绩] = credit.成绩
            s[最终成绩] = credit.最终成绩
        }
    }

    suspend fun getCredits(username: String) = startSuspendTransaction(Credit) {
        select(username(username)).toEntities().sorted()
    }

    suspend fun addCreditOverviews(
        username: String,
        overviews: List<cn.luckcc.fjmu.lib.proto.CreditOverview>,
        onUpdate: ((changes: List<Triple<String, Float, Float>>) -> Unit)? = null
    ) = startSuspendTransaction(CreditOverview) {
        if (overviews.isEmpty())
            return@startSuspendTransaction
        if (onUpdate != null) {
            val olds = getCreditOverviews(username)
            val changes = arrayListOf<Triple<String, Float, Float>>()
            val names = (overviews.map { it.绩点分类名称 } + olds.map { it.绩点分类名称 }).distinct()
            for (name in names) {
                val old = olds.find { it.绩点分类名称 == name }?.已获学分 ?: 0f
                val new = overviews.find { it.绩点分类名称 == name }?.已获学分 ?: 0f
                if (old == new) continue
                changes.add(Triple(name, old, new))
            }
            onUpdate(changes)
        }
        batchUpsert(overviews) { s, overview ->
            s[this.username] = username
            s[绩点分类名称] = overview.绩点分类名称
            s[要求学分] = overview.要求学分
            s[已获学分] = overview.已获学分
        }
    }

    suspend fun getCreditOverviews(
        username: String
    ) = startSuspendTransaction(CreditOverview) {
        select(username(username)).toEntities()
    }

    suspend fun addMessages(
        username: String,
        messages: List<cn.luckcc.fjmu.lib.proto.Message>,
        onUpdate: ((List<cn.luckcc.fjmu.lib.proto.Message>) -> Unit)? = null
    ) = startSuspendTransaction(Message) {
        if (messages.isEmpty()) {
            return@startSuspendTransaction
        }
        if (onUpdate != null) {
            onUpdate(selectNewData(消息ID, messages) { it.消息ID })
        }
        batchUpsert(messages) { s, message ->
            s[this.username] = username
            s[标题] = message.标题
            s[内容] = message.内容
            s[消息类型] = message.消息类型
            s[消息类型Id] = message.消息类型ID
            s[epochMilli] = message.epochMilli
            s[消息ID] = message.消息ID
            s[已读] = message.已读
        }
    }

    suspend fun getMessages(
        username: String,
    ) = startSuspendTransaction(Message) {
        select(username(username)).toEntities()
    }

    suspend fun getMessages(
        username: String, messageType: String
    ) = startSuspendTransaction(Message) {
        select(username(username) and messageType(messageType)).toEntities()
    }

    suspend fun getMessageTypes(username: String) = startSuspendTransaction(Message) {
        slice(消息类型).select(username(username)).withDistinct().map { it[消息类型] }
    }

    suspend fun addRanks(
        username: String,
        ranks: List<cn.luckcc.fjmu.lib.proto.Rank>,
        onUpdate: ((changeList: List<Triple<Int, Float, Float>>, selfChange: Pair<Pair<Int, Float>, Pair<Int, Float>>?) -> Unit)? = null
    ) = startSuspendTransaction(Rank) {
        if (ranks.isEmpty())
            return@startSuspendTransaction
        if (onUpdate != null) {
            val oldRanks = getRanks(username)

            val changeList = arrayListOf<Triple<Int, Float, Float>>()
            val count = max(ranks.size, oldRanks.size)
            var oldSelf = oldRanks.find { it.isSelf }?.let {
                it.班级排名 to it.平均绩点
            } ?: Pair(0, 0f)
            var newSelf = ranks.find { it.isSelf }?.let {
                it.班级排名 to it.平均绩点
            } ?: Pair(0, 0f)
            val selfChange = if (oldSelf == newSelf) null
            else oldSelf to newSelf
            for (index in 0 until count) {
                val new = ranks.getOrNull(index)
                val old = oldRanks.getOrNull(index)
                val newCredit = new?.平均绩点 ?: 0f
                val oldCredit = old?.平均绩点 ?: 0f
                if (oldCredit == oldCredit) continue
                changeList.add(Triple(index, oldCredit, newCredit))
            }
            onUpdate(changeList, selfChange)
        }
        batchUpsert(ranks) { s, rank ->
            s[this.username] = username
            s[isSelf] = rank.isSelf
            s[班级排名] = rank.班级排名
            s[平均绩点] = rank.平均绩点
            s[姓名] = rank.姓名
        }
    }

    suspend fun getRanks(username: String) = startSuspendTransaction(Rank) {
        select(username(username)).toEntities()
    }


    suspend fun addBulletins(
        bulletins: List<cn.luckcc.fjmu.lib.proto.Bulletin>,
        onUpdate: ((new: List<cn.luckcc.fjmu.lib.proto.Bulletin>) -> Unit)? = null
    ) = startSuspendTransaction {
        if (bulletins.isEmpty()) return@startSuspendTransaction
        if (onUpdate != null) {
            val bIds = bulletins.map { it.newsId }
            val oldIds = with(Bulletin) {
                slice(newsId).select(newsId inList bIds).map { it[newsId] }
            }
            onUpdate(bulletins.filter { it.newsId !in oldIds })
        }
        Bulletin.batchUpsert(bulletins) { s, bulletin ->
            s[newsId] = bulletin.newsId
            s[标题] = bulletin.标题
            s[部门名称] = bulletin.部门名称
            s[epochDay] = bulletin.epochDay
            s[内容] = bulletin.内容
            s[链接] = bulletin.链接
        }
        for (bulletin in bulletins) {
            if (bulletin.isEmpty()) continue
            BulletinAttachment.batchUpsert(bulletin) { s, attachment ->
                s[newsId] = bulletin.newsId
                s[附件名称] = attachment.附件名称
                s[附件下载链接] = attachment.附件下载链接
            }
        }
    }

    suspend fun getAllBulletinDates() = startSuspendTransaction(Bulletin) {
        slice(epochDay).selectAll().withDistinct().map { it[epochDay].epochDay }
    }

    suspend fun getBulletinsAtDate(epochDay: Long) = startSuspendTransaction(Bulletin) {
        val bulletins = select(Bulletin.epochDay eq epochDay).toEntities()
        applyAttachments(bulletins)
        bulletins
    }

    suspend fun getBulletinsWithReg(regPattern:String)=startSuspendTransaction(Bulletin){
        val bulletins=select((标题 regexp regPattern)or (内容 regexp regPattern)).toEntities()
        applyAttachments(bulletins)
        bulletins
    }

    suspend fun getBulletin(newsId: Int) = startSuspendTransaction(Bulletin) {
        val bulletin = select(Bulletin.newsId eq newsId).limit(1).singleEntity()?.apply {
            applyAttachments(listOf(this))
        }
        bulletin
    }

    suspend fun getBulletins(epochDay: Long)=startSuspendTransaction {
        val bulletins= with(Bulletin){
            select(Bulletin.epochDay eq epochDay).toEntities()
        }
        applyAttachments(bulletins)
        bulletins
    }

    suspend fun getBulletins(
        startDay: Long, endDay: Long
    ) = startSuspendTransaction {
        val bulletins = with(Bulletin) {
            select(dayRange(startDay, endDay)).toEntities()
        }
        applyAttachments(bulletins)
        bulletins
    }

    private inline fun applyAttachments(bulletins: List<cn.luckcc.fjmu.lib.proto.Bulletin>) {
        if (bulletins.isEmpty())
            return
        val bIDs = bulletins.map { it.newsId }.distinct()
        val attachmentsMap = with(BulletinAttachment) {
            select(newsId inList bIDs).groupBy(
                keySelector = {
                    it[newsId]
                }, valueTransform = this::toEntity
            )
        }
        for (bulletin in bulletins) {
            val attachments = attachmentsMap[bulletin.newsId] ?: emptyList()
            bulletin.addAll(attachments)
        }
    }


    suspend fun getCourseData(
        username: String, semesterPair: ISemesterPair
    )=getCourseData(
        username, semesterPair, null, null
    ) { classrooms, teachers, courses, teachingClasses, faculties, clazz, start ->
        SemesterCourse(
            semesterPair.toYearSemester(start),
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz,
        )
    }


    suspend fun getCourseData(
        username: String, semesterPair: ISemesterPair, weekOfSemester: Int
    )=getCourseData(
        username, semesterPair, weekOfSemester, null
    ) { classrooms, teachers, courses, teachingClasses, faculties, clazz, start ->
        WeekCourse(
            semesterPair.toYearSemester(start),
            weekOfSemester,
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz,
        )
    }

    suspend fun getCourseData(
        username: String, semesterPair: ISemesterPair, weekOfSemester: Int, dayOfWeek: DayOfWeek
    )=getCourseData(
        username, semesterPair, weekOfSemester, dayOfWeek
    ) { classrooms, teachers, courses, teachingClasses, faculties, clazz, start ->
        DayCourse(
            semesterPair.toYearSemester(start),
            weekOfSemester,
            dayOfWeek,
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz,
        )
    }

    internal suspend fun <T> getCourseData(
        username: String, optionalOp: (Clazz.() -> Op<Boolean>)? = null, mapper: (
            classrooms: List<cn.luckcc.fjmu.lib.proto.course.Classroom>,
            teachers: List<cn.luckcc.fjmu.lib.proto.course.Teacher>,
            courses: List<cn.luckcc.fjmu.lib.proto.course.Course>,
            teachingClasses: List<cn.luckcc.fjmu.lib.proto.course.TeachingClass>,
            faculties: List<Faculty>,
            clazz: List<cn.luckcc.fjmu.lib.proto.course.Clazz>,
        ) -> T
    ) = startSuspendTransaction {
        val op = with(Clazz) {
            var op: Op<Boolean> = username(username)
            if (optionalOp != null) {
                op = (op and optionalOp())
            }
            op
        }
        val clazz = with(Clazz) {
            select(op).toEntities()
        }
        if (clazz.isEmpty()) {
            return@startSuspendTransaction mapper(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        }
        val tripleCourses = clazz.map {
            Triple(it.学年, it.学期, it.课程ID)
        }.distinct()
        val courses = with(Course) {
            select(username(username) and (Triple(学年, 学期, 课程ID) inList tripleCourses)).toEntities()
        }
        val tripleTeachingClass = clazz.map {
            Triple(it.学年, it.学期, it.教学班名称)
        }.distinct()
        val teachingClasses = with(TeachingClass) {
            select(username(username) and (Triple(学年, 学期, 教学班名称) inList tripleTeachingClass)).toEntities()
        }
        val classrooms = with(Classroom) {
            select(教室名称 inList clazz.map { it.教室名称 }.distinct()).toEntities()
        }
        val teachers = with(Teacher) {
            select(教师ID inList clazz.map { it.教师ID }.distinct()).toEntities()
        }
        val faculties = (teachers.map { it.部门名称 } + courses.map { it.开课学院 }).distinct().map {
            Faculty(it)
        }
        mappingData(classrooms, teachers, courses, teachingClasses, faculties, clazz)
        applyCourse(courses, username, optionalOp)
        mapper(classrooms, teachers, courses, teachingClasses, faculties, clazz)
    }

    private suspend inline fun <T : ICourseDataContainer> getCourseData(
        username: String,
        semesterPair: ISemesterPair,
        weekOfSemester: Int? = null,
        dayOfWeek: DayOfWeek? = null,
        crossinline mapper: (
            classrooms: List<cn.luckcc.fjmu.lib.proto.course.Classroom>, teachers: List<cn.luckcc.fjmu.lib.proto.course.Teacher>, courses: List<cn.luckcc.fjmu.lib.proto.course.Course>, teachingClasses: List<cn.luckcc.fjmu.lib.proto.course.TeachingClass>, faculties: List<Faculty>, clazz: List<cn.luckcc.fjmu.lib.proto.course.Clazz>, startDay: Long
        ) -> T
    ): T {
        val (schoolYear,semester)=semesterPair
        val startDay = startSuspendTransaction(SemesterTable) {
            selectStartDay(schoolYear, semester)
        }
            ?: throw IllegalStateException("Cannot get startDay from SemesterTable,please check if no data is applied to $schoolYear-$semester")
        return getCourseData(
            username = username,
            optionalOp = {
                atSpecifiedDay(schoolYear, semester, weekOfSemester, dayOfWeek)
            },
        ) { classrooms, teachers, courses, teachingClasses, faculties, clazz ->
            mapper(classrooms, teachers, courses, teachingClasses, faculties, clazz, startDay)
        }
    }

    suspend fun getAllSemesters(username: String) = startSuspendTransaction {
        val semesters= with(Course){
            slice(学年, 学期).select(username(username)).withDistinct().map { r ->
                r[学年] to r[学期]
            }
        }
        with(SemesterTable){
            val p=(学年 to 学期)
            select(p inList semesters).toEntities()
        }.sorted()
    }

    suspend fun getAllSemesters() = startSuspendTransaction(SemesterTable) {
        selectAll().toEntities().sorted()
    }

    private fun applyCourse(
        courses: List<cn.luckcc.fjmu.lib.proto.course.Course>,
        username: String,
        optionalOp: (Clazz.() -> Op<Boolean>)? = null
    ) {
        val 学时类型 = with(Clazz) {
            var op: Op<Boolean> = username(username)
            if (optionalOp != null) op = (op and optionalOp())
            val 学时数 = 学时.sum()
            slice(课程ID, 学时类型, 学时数).select(op).groupBy(课程ID, 学时类型).groupBy(keySelector = {
                it[课程ID]
            }, valueTransform = {
                Pair(it[学时类型], it[学时数] ?: 0)
            })
        }
        for (course in courses) {
            course.学时分类 = 学时类型[course.课程ID] ?: emptyList()
        }
    }

    suspend fun getAllCourseInfo(username: String, semesterPair: ISemesterPair) = startSuspendTransaction {
        val (schoolYear,semester)=semesterPair
        val courses = with(Course) {
            select(op(username, schoolYear, semester)).toEntities()
        }
        applyCourse(courses, username) {
            atSemester(schoolYear, semester)
        }
        courses
    }

    suspend fun getAllCourseInfo(username: String) = startSuspendTransaction {
        val courses = with(Course) {
            select(username(username)).toEntities()
        }
        applyCourse(courses, username)
        courses
    }

    suspend fun getCourse(username: String, semesterPair: ISemesterPair, courseId: String): cn.luckcc.fjmu.lib.proto.course.Course?{
        val (schoolYear,semester)=semesterPair
        return getCourseData(username = username, optionalOp = {
            atSemester(schoolYear, semester) and (课程ID eq courseId)
        }, mapper = { _, _, courses, _, _, _ ->
            courses.firstOrNull()
        })
    }

    suspend fun updateClazz(
        username: String,
        clazz:List<cn.luckcc.fjmu.lib.proto.course.Clazz>
    )=startSuspendTransaction(Clazz){
        batchUpsert(clazz){s,clazz->
            s[this.username] = username
            s[学年] = clazz.学年
            s[学期] = clazz.学期
            s[课程ID] = clazz.课程ID
            s[教学班ID] = clazz.教学班ID
            s[教学班名称] = clazz.教学班名称
            s[教师ID] = clazz.教师ID
            s[教室名称] = clazz.教室名称
            s[课次] = clazz.课次
            s[dayOfWeek] = clazz.dayOfWeek
            s[weekOfSemester] = clazz.weekOfSemester
            s[内容] = clazz.内容
            s[课程性质] = clazz.课程性质
            s[学时类型] = clazz.学时类型
            s[上课要求] = clazz.上课要求
            s[epochDay] = clazz.epochDay
            s[上课开始节次] = clazz.上课开始节次
            s[学时] = clazz.学时
            s[备注] = clazz.备注
            s[refCalendarEventId] = clazz.refCalendarEventId
        }
    }
    suspend fun addCourseDataFor(
        username: String,
        semesterCourse: SemesterCourse,
        onUpdate: ((removes: List<cn.luckcc.fjmu.lib.proto.course.Clazz>, appends: List<cn.luckcc.fjmu.lib.proto.course.Clazz>) -> Unit)? = null
    ) {
        if (semesterCourse.isEmpty) return
        val (schoolYear,semester,startOfSemester)=semesterCourse.yearSemester
        if (onUpdate != null) {
            val clazz=getCourseData(
                username = username,
                optionalOp = {
                    atSemester(schoolYear,semester)
                },
            ) { _, _, _, _, _, clazz ->
                clazz
            }
            val old = clazz.toSet()
            val new = semesterCourse.clazz.toSet()
            val appends = new - old
            val removes = old - new
            onUpdate(removes.toList(), appends.toList())
        }
        startSuspendTransaction {
            SemesterTable.upsert {
                it[学年] = schoolYear
                it[学期] = semester
                it[start] = startOfSemester.epochDay
            }
            TeachingClass.deleteWhere {
                username(username) and schoolYear(schoolYear) and semester(semester)
            }
            Clazz.deleteWhere {
                username(username) and schoolYear(schoolYear) and semester(semester)
            }
            Course.deleteWhere {
                username(username) and schoolYear(schoolYear) and semester(semester)
            }
            Classroom.batchUpsert(semesterCourse.classrooms) { stat, classroom ->
                stat[教室名称] = classroom.教室名称
                stat[教室类型] = classroom.教室类型
                stat[教室ID] = classroom.教室ID
                stat[教学楼] = classroom.教学楼
                stat[校区] = classroom.校区
            }
            Teacher.batchUpsert(semesterCourse.teachers) { s, teacher ->
                s[教师ID] = teacher.教工号
                s[姓名] = teacher.姓名
                s[部门名称] = teacher.部门名称
                s[机构] = teacher.机构
            }
            TeachingClass.batchUpsert(semesterCourse.teachingClasses) { s, teachingClass ->
                s[this.username] = username
                s[学年] = schoolYear
                s[学期] = semester
                s[教学班ID] = teachingClass.教学班ID
                s[教学班名称] = teachingClass.教学班名称
                s[课程ID] = teachingClass.课程ID
                s[教学班组成] = teachingClass.教学班组成
            }

            Clazz.batchUpsert(semesterCourse.clazz) { s, clazz ->
                s[this.username] = username
                s[学年] = schoolYear
                s[学期] = semester
                s[课程ID] = clazz.课程ID
                s[教学班ID] = clazz.教学班ID
                s[教学班名称] = clazz.教学班名称
                s[教师ID] = clazz.教师ID
                s[教室名称] = clazz.教室名称
                s[课次] = clazz.课次
                s[dayOfWeek] = clazz.dayOfWeek
                s[weekOfSemester] = clazz.weekOfSemester
                s[内容] = clazz.内容
                s[课程性质] = clazz.课程性质
                s[学时类型] = clazz.学时类型
                s[上课要求] = clazz.上课要求
                s[epochDay] = clazz.epochDay
                s[上课开始节次] = clazz.上课开始节次
                s[学时] = clazz.学时
                s[备注] = clazz.备注
                s[refCalendarEventId] = clazz.refCalendarEventId
            }

            Course.batchUpsert(semesterCourse.courses) { s, course ->
                s[this.username] = username
                s[学期] = course.学期
                s[学年] = course.学年
                s[课程ID] = course.课程ID
                s[课程名称] = course.课程名称
                s[assessment] = course.assessment
                s[examination] = course.examination
                s[examinationMethod] = course.examinationMethod
                s[type] = course.type
                s[课程类型] = course.type
                s[课程性质] = course.课程性质
                s[type2] = course.type2
                s[开课学院] = course.开课学院
                s[学分] = course.学分
                s[index] = course.index
            }
        }
    }

    suspend fun removeUserData(username: String) = startSuspendTransaction {
        Clazz.removeUserData(username)
        Course.removeUserData(username)
        Credit.removeUserData(username)
        CreditOverview.removeUserData(username)
        Message.removeUserData(username)
        Rank.removeUserData(username)
        TeachingClass.removeUserData(username)
        BookRequest.removeUserData(username)
        CetResult.removeUserData(username)
    }

    override fun close() {
        TransactionManager.closeAndUnregister(db)
    }
}