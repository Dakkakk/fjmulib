@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Campus
import cn.luckcc.fjmu.lib.FormData
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.proto.course.Classroom
import cn.luckcc.fjmu.lib.proto.course.Clazz
import cn.luckcc.fjmu.lib.proto.course.Teacher
import cn.luckcc.fjmu.lib.proto.course.TeachingClass
import zx.dkk.utils.collections.NONE
import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.epochMilli
import zx.dkk.utils.time.now
import zx.dkk.utils.time.nowDate
import zx.dkk.utils.time.yyyyMMdd
import java.time.DayOfWeek
import java.time.LocalDate

fun courseDailyUrl(username:String)
    ="https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/design/funcData_cxFuncDataList.html?func_widget_guid=C09A30BEA9F01D68E0530100007FFB6B&gnmkdm=N219943&su=${username}"
fun makeCourseDailyRequestBody(schoolYear:Int, semester: Semester, teachingClassName:String): FormData {
    return FormData(
        "xnm" to schoolYear.toString(),
        "xqm" to semester.`val`,
        "jxbmc" to teachingClassName,
        "_search" to "false",
        "nd" to now.epochMilli.toString(),
        "queryModel.showCount" to "100",
        "queryModel.currentPage" to "1",
        "queryModel.sortOrder" to "asc",
        "queryModel.sortName" to "",
        "time" to "1",
    )
}
@Serializable
data class CourseDailyResponse(
    @SerialName("currentPage")
    val currentPage: Int = 0, // 1
    @SerialName("currentResult")
    val currentResult: Int = 0, // 0
    @SerialName("entityOrField")
    val entityOrField: Boolean = false, // false
    @SerialName("items")
    val items: List<CourseDaily> = listOf(),
    @SerialName("limit")
    val limit: Int = 0, // 15
    @SerialName("offset")
    val offset: Int = 0, // 0
    @SerialName("pageNo")
    val pageNo: Int = 0, // 0
    @SerialName("pageSize")
    val pageSize: Int = 0, // 15
    @SerialName("showCount")
    val showCount: Int = 0, // 15
    @SerialName("sortName")
    val sortName: String = "",
    @SerialName("sortOrder")
    val sortOrder: String = "", // asc
    @SerialName("totalCount")
    val totalCount: Int = 0, // 290
    @SerialName("totalPage")
    val totalPage: Int = 0, // 20
    @SerialName("totalResult")
    val totalResult: Int = 0 // 290
)
@Serializable
data class CourseDaily(
    @SerialName("cd_id")
    val 教室ID: String = NONE, // D01C82
    @SerialName("cdmc")
    val 教室名称: String = NONE, // 82教
    @SerialName("jc")
    val 上课节次: String = NONE, // 10,11
    @SerialName("jg_id")
    val jgId: String = NONE, // 10
    @SerialName("jgmc")
    val 任课教师部门: String = NONE, // 临床医学院
    @SerialName("jxb_id")
    val 教学班ID: String = NONE, // C44C805E710E1CCBE0530100007FFA55
    @SerialName("jxbmc")
    val 教学班名称: String = NONE, // 出生缺陷防治知多少-0001
    @SerialName("jxbrs")
    val 教学班人数: Int = 0, // 100
    @SerialName("jxbzc")
    val 教学班组成: String = NONE, // 无
    @SerialName("kc")
    val kc: Int=1, // 1
    @SerialName("kch")
    val 课程ID: String = NONE, // r820104
    @SerialName("kcmc")
    val 课程名称: String = NONE, // 出生缺陷防治知多少
    @SerialName("kcxz")
    val 课程性质: String = NONE, // 任意选修课
    @SerialName("ksl")
    val ksl: String = NONE, // 2
    @SerialName("node_desc")
    val nodeDesc: String = NONE, // 通过
    @SerialName("rkjs")
    val 任课教师ID: String = NONE, // 9201761074
    @SerialName("rkjs_jg_id")
    val rkjsJgId: String = NONE, // 82
    @SerialName("rkjs_jgmc")
    val 任课教师机构: String = NONE, // 附属福建省妇幼保健院(妇儿临床医学院)
    @SerialName("rkjsxm")
    val 任课教师姓名: String = NONE, // 余爱丽
    @SerialName("row_id")
    val rowId: Int = 0, // 1
    @SerialName("rq")
    val 上课日期: String = NONE, // 2021-09-23
    @SerialName("shzt")
    val shzt: String = NONE, // 通过
    @SerialName("shzt_id")
    val shztId: String = NONE, // 3
    @SerialName("sknr")
    val 上课内容: String = NONE, // 出生缺陷防治概述
    @SerialName("skyq")
    val 上课要求: String = NONE, // 课前在学习通上完成问卷调查。
    @SerialName("totalresult")
    val totalresult: Int = 0, // 6
    @SerialName("xkrs")
    val xkrs: Int = 0, // 100
    @SerialName("xn")
    val xn: String = NONE, // 2021-2022
    @SerialName("xnm")
    val xnm: String = NONE, // 2021
    @SerialName("xq")
    val xq: String = NONE, // 1
    @SerialName("xqj")
    val xqj: Int=1, // 4
    @SerialName("xqm")
    val xqm: String = NONE, // 3
    @SerialName("xsdm")
    val xsdm: String = NONE, // 01
    @SerialName("xsmc")
    val 学时类型: String = NONE, // 讲课
    @SerialName("zc")
    val 周次: Int=1, // 3
    @SerialName("zcm")
    val zcm: String = NONE, // 无
    @SerialName("bz")
    val 备注:String= NONE
){
    fun asKlass(学年: Int, 学期: Semester, ): Clazz {
        val jcs = 上课节次.split(",")
        val start = jcs.first().toInt()
        val end = jcs.last().toInt()
        return Clazz(
            课程ID = 课程ID,
            教学班ID = 教学班ID,
            教学班名称 = 教学班名称,
            内容 = 上课内容.replace(Regex(" +"), " ").replace("\n", ""),
            dayOfWeek = DayOfWeek.of(xqj),
            weekOfSemester = 周次,
            教师ID = 任课教师ID,
            教室名称 = 教室名称,
            课次 = kc,
            备注 = 备注,
            学时 = end-start+1,
            上课开始节次 = start,
            学时类型 = 学时类型,
            课程性质 = 课程性质,
            上课要求 = 上课要求,
            epochDay = (if (上课日期 != NONE)
                 LocalDate.parse(
                    上课日期,
                     yyyyMMdd
                )else nowDate).epochDay,
            学期 = 学期,
            学年 = 学年,
            refCalendarEventId = null
        )
    }

    fun asTeachingClass(学年: Int, 学期: Semester, )= TeachingClass(
        教学班ID = 教学班ID,
        教学班名称 = 教学班名称,
        课程ID = 课程ID,
        教学班组成 = 教学班组成,
        学期 = 学期,
        学年 = 学年,
    )

    fun asTeacher()= Teacher(
        姓名 = 任课教师姓名,
        教工号 = 任课教师ID,
        部门名称 = 任课教师部门,
        机构 = 任课教师机构,
    )

    fun asClassroom()= Classroom(
        教室ID = 教室ID,
        教室名称 = 教室名称.replace("号楼", "#"),
        校区 = Campus.UnConfined,
        教学楼  = "",
        教室类型 = "",
    )
}