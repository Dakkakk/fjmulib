@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.database.UsernameScheduleTable
import cn.luckcc.fjmu.lib.proto.course.Clazz
import java.time.DayOfWeek

object Clazz: UsernameScheduleTable<Clazz>("clazz") {
    val 课程ID= text("courseId")
    val 教学班ID=text("teachingClassId")
    val 教学班名称=text("teachingClassName")
    val 教师ID=text("teacherId")
    val 教室名称=text("classroomName")
    /**
     * 课次，该次课是相应课程的第几次课
     */
    val 课次=integer("countOfCourse")
    val dayOfWeek=enumeration<DayOfWeek>("dayOfWeek")
    val weekOfSemester=integer("weekOfSemester")
    val 内容=text("content")
    val 课程性质=text("type")
    val 学时类型=text("type2")
    val 上课要求=text("requirement")
    val epochDay=long("epochDay")
    val 上课开始节次=integer("start")
    val 学时=integer("count")
    val 备注=text("comment")
    val refCalendarEventId=long("refCalendarId").nullable().default(null)
    @Suppress("NOTHING_TO_INLINE")
    inline fun opWithWeekNDay(username: String, 学年: Int, 学期: Semester, weekOfSemester:Int?, dayOfWeek: DayOfWeek?): Op<Boolean> {
        var initial=op(username, 学年, 学期)
        if (weekOfSemester!=null)
            initial=(initial and (cn.luckcc.fjmu.lib.database.course.Clazz.weekOfSemester eq weekOfSemester))
        if (dayOfWeek!=null)
            initial=(initial and (cn.luckcc.fjmu.lib.database.course.Clazz.dayOfWeek eq dayOfWeek))
        return initial
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun atSpecifiedDay(学年: Int, 学期: Semester, weekOfSemester:Int?, dayOfWeek: DayOfWeek?):Op<Boolean>{
        var initial=atSemester(学年, 学期)
        if (weekOfSemester!=null)
            initial=(initial and (cn.luckcc.fjmu.lib.database.course.Clazz.weekOfSemester eq weekOfSemester))
        if (dayOfWeek!=null)
            initial=(initial and (cn.luckcc.fjmu.lib.database.course.Clazz.dayOfWeek eq dayOfWeek))
        return initial
    }
    override fun toEntity(username: String, 学年: Int, 学期: Semester, r: ResultRow): Clazz {
        return Clazz(
            课程ID = r[课程ID],
            教学班ID = r[教学班ID],
            教学班名称 = r[教学班名称],
            教师ID = r[教师ID],
            教室名称 = r[教室名称],
            课次 = r[课次],
            dayOfWeek = r[dayOfWeek],
            weekOfSemester = r[weekOfSemester],
            内容 = r[内容],
            课程性质 = r[课程性质],
            学时类型 = r[学时类型],
            上课要求 = r[上课要求],
            epochDay=r[epochDay],
            上课开始节次 = r[上课开始节次],
            学时 = r[学时],
            备注 = r[备注],
            学年 = 学年,
            学期 = 学期,
            refCalendarEventId = r[refCalendarEventId]
        )
    }

    override val primaryKey=PrimaryKey(
        username,学年,学期, 课程ID, 教学班名称, epochDay, 上课开始节次, 学时
    )
}