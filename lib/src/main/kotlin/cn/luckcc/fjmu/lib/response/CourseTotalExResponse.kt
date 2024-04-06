@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.FormData
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.proto.course.Course
import cn.luckcc.fjmu.lib.proto.course.Faculty
import zx.dkk.utils.time.epochMilli
import zx.dkk.utils.time.now

fun courseExUrl(username:String)
="https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/jxjcgl/jxjcwh_cxJxjcqkIndex.html?doType=query&gnmkdm=N155310&su=$username"
fun courseExRequestBody(schoolYear:Int, semester: Semester, teachingClassName:String): FormData {
    return FormData(
        "xnm" to schoolYear.toString(),
        "xqm" to semester.`val`,
        "jxbmc" to teachingClassName,
        "cxfs" to "1",
        "_search" to "false",
        "nd" to now.epochMilli.toString(),
        "queryModel.showCount" to "100",
        "queryModel.currentPage" to "1",
        "time" to "1"
    )
}
@Serializable
data class CourseTotalExResponse(
    @SerialName("currentPage")
    val currentPage: Int = 0, // 1
    @SerialName("currentResult")
    val currentResult: Int = 0, // 0
    @SerialName("entityOrField")
    val entityOrField: Boolean = false, // false
    @SerialName("items")
    val items: List<CourseExItem> = listOf(),
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
    @SerialName("totalCount")
    val totalCount: Int = 0, // 3
    @SerialName("totalPage")
    val totalPage: Int = 0, // 1
    @SerialName("totalResult")
    val totalResult: Int = 0 // 3
)

@Serializable
data class CourseExItem(
    @SerialName("apxs")
    val apxs: Int = 0, // 0
    @SerialName("cdlbmc")
    val cdlbmc: String = "", // 多媒体教室
    @SerialName("date")
    val date: String = "", // 二○二二年八月十五日
    @SerialName("dateDigit")
    val dateDigit: String = "", // 2022年8月15日
    @SerialName("dateDigitSeparator")
    val dateDigitSeparator: String = "", // 2022-8-15
    @SerialName("day")
    val day: String = "", // 15
    @SerialName("jgh")
    val jgh: String = "", // 9202001081,9201511022,9200811023,9202101001,9199401021,9200911034
    @SerialName("jgpxzd")
    val jgpxzd: String = "", // 1
    @SerialName("jsbm")
    val jsbm: String = "", // 医学技术与工程学院,附属第一医院（第一临床医学院）,附属第一医院（第一临床医学院）,医学技术与工程学院,医学技术与工程学院,附属第一医院（第一临床医学院）
    @SerialName("jsjxrlb_id")
    val jsjxrlbId: String = "", // E31CE6BA1BA60107E0530100007F6A03
    @SerialName("jsxm")
    val jsxm: String = "", // 黄烙明,陈鲤敏,谢茂松,陈重江,黄焱(医技),叶琴(附一)
    @SerialName("jxb_id")
    val teachingClassId: String = "", // E1586FADFB0F6724E0530100007FCD57
    @SerialName("jxbmc")
    val 教学班名称: String = "", // 眼视光器械学-0001
    @SerialName("jxbzc")
    val jxbzc: String = "", // 20眼视光
    @SerialName("jxdd")
    val jxdd: String = "", // 85教;85教;85教;85教
    @SerialName("kcgsmc")
    val kcgsmc: String = "", // 无
    @SerialName("kch")
    val 课程ID: String = "", // 0550163
    @SerialName("kch_id")
    val kchId: String = "", // 0550163
    @SerialName("kclbmc")
    val kclbmc: String = "", // 必修课程
    @SerialName("kcmc")
    val 课程名称: String = "", // 眼视光器械学
    @SerialName("kcxzmc")
    val 课程性质: String = "", // 专业课
    @SerialName("khfsmc")
    val 考核方式: String = "", // 考试
    @SerialName("kkbm_id")
    val kkbmId: String = "", // 05
    @SerialName("kklxmc")
    val 课程类型: String = "", // 主修课程
    @SerialName("kkxy")
    val 开课学院: String = "", // xxxxx
    @SerialName("ksfsmc")
    val 考试方式: String = "", // 笔试
    @SerialName("ksxsmc")
    val 考试形式: String = "", // 统一
    @SerialName("listnav")
    val listnav: String = "", // false
    @SerialName("localeKey")
    val localeKey: String = "", // zh_CN
    @SerialName("lrjsxx")
    val lrjsxx: String = "", // 9202001081/黄烙明
    @SerialName("month")
    val month: String = "", // 8
    @SerialName("pageTotal")
    val pageTotal: Int = 0, // 0
    @SerialName("pageable")
    val pageable: Boolean = false, // true
    @SerialName("qsjsz")
    val qsjsz: String = "", // 1-18周
    @SerialName("rangeable")
    val rangeable: Boolean = false, // true
    @SerialName("rlzt")
    val rlzt: String = "", // 3
    @SerialName("rlztmc")
    val rlztmc: String = "", // 已通过
    @SerialName("row_id")
    val rowId: String = "", // 1
    @SerialName("sfzsjkzfwn")
    val sfzsjkzfwn: Boolean = false, // false
    @SerialName("skcs")
    val skcs: Int = 0, // 0
    @SerialName("sksj")
    val sksj: String = "", // 星期三第1-2节{4-5周,7周};星期三第3-4节{8-9周};星期四第1-2节{4-5周,7-9周};星期四第3-5节{10-11周,13-14周}
    @SerialName("totalResult")
    val totalResult: String = "", // 3
    @SerialName("xf")
    val 学分: String = "", // 3.0
    @SerialName("xiaoqmc")
    val xiaoqmc: String = "", // 旗山校区
    @SerialName("xnm")
    val xnm: String = "", // 2022
    @SerialName("xnmc")
    val xnmc: String = "", // 2022-2023
    @SerialName("xqm")
    val xqm: String = "", // 3
    @SerialName("xqmc")
    val xqmc: String = "", // 1
    @SerialName("xsdm")
    val xsdm: String = "", // 01
    @SerialName("xsmc")
    val xsmc: String = "", // 讲课
    @SerialName("year")
    val year: String = "", // 2022
    @SerialName("zhxs")
    val zhxs: String = "", // 3
    @SerialName("zxs")
    val zxs: String = "", // 54
    @SerialName("zxxs")
    val zxxs: Int = 0 // 0
){
    @Suppress("NOTHING_TO_INLINE")
    inline fun asCourse(学年: Int, 学期: Semester)
    = Course(
        课程ID = 课程ID,
        课程名称 = 课程名称,
        assessment = 考核方式,
        学分 = 学分,
        开课学院 = 开课学院,
        type2 = kcgsmc,
        课程性质 = 课程性质,
        课程类型 = 课程类型,
        examinationMethod = 考试形式,
        examination = 考试方式,
        type = kclbmc,
        学年 = 学年,
        学期 = 学期,
    )
    @Suppress("NOTHING_TO_INLINE")
    inline fun asFaculty()
    = Faculty(
        部门名称 = 开课学院,
    )
}