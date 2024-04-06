@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import cn.luckcc.fjmu.lib.FormData
import cn.luckcc.fjmu.lib.Semester
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.course.TeachingClass

fun courseTotalUrl() =
    "https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=N2151"

fun makeCourseTotalRequestBody(schoolYear: Int, semester: Semester): FormData {
    return FormData(
        "xnm" to schoolYear.toString(),
        "xqm" to semester.`val`,
        "kclx" to "ck",
        "xsdm" to ""
    )
}

@Serializable
data class CourseTotalResponse(
    @SerialName("currentPage")
    val currentPage: Int = 0, // 1
    @SerialName("currentResult")
    val currentResult: Int = 0, // 0
    @SerialName("entityOrField")
    val entityOrField: Boolean = false, // false
    @SerialName("kbList")
    val items: List<CourseTotal> = listOf(),
    @SerialName("limit")
    val limit: Int = 0, // 15
    @SerialName("offset")
    val offset: Int = 0, // 0
    @SerialName("pageNo")
    val pageNo: Int = 0, // 0
    @SerialName("pageSize")
    val pageSize: Int = 0, // 15
    @SerialName("showCount")
    val showCount: Int = 0, // 500
    @SerialName("sortOrder")
    val sortOrder: String = "", // asc
    @SerialName("totalCount")
    val totalCount: Int = 0, // 10
    @SerialName("totalPage")
    val totalPage: Int = 0, // 1
    @SerialName("totalResult")
    val totalResult: Int = 0 // 10
)

@Serializable
data class CourseTotal(
    @SerialName("apxs")
    val apxs: Int = 0, // 0
    @SerialName("cdlbmc")
    val cdlbmc: String = "", // 多媒体教室
    @SerialName("date")
    val date: String = "", // 二○二二年八月十一日
    @SerialName("dateDigit")
    val dateDigit: String = "", // 2022年8月11日
    @SerialName("dateDigitSeparator")
    val dateDigitSeparator: String = "", // 2022-8-11
    @SerialName("day")
    val day: String = "", // 11
    @SerialName("jgh")
    val jgh: String = "", // 9199401021,9201201010,9201825023,9200911034
    @SerialName("jgpxzd")
    val jgpxzd: String = "", // 1
    @SerialName("jsbm")
    val jsbm: String = "", // 医学技术与工程学院,医学技术与工程学院,附属第一医院（第一临床医学院）,附属第一医院（第一临床医学院）
    @SerialName("jsjxrlb_id")
    val jsjxrlbId: String = "", // E2F9020DCB6D0471E0530100007FDADC
    @SerialName("jsxm")
    val jsxm: String = "", // 黄焱(医技),董骋寰,张晶津,叶琴(附一)
    @SerialName("jxb_id")
    val 教学班ID: String = "", // E1586FADFAF16724E0530100007FCD57
    @SerialName("jxbmc")
    val 教学班名称: String = "", // 眼科学基础-0001
    @SerialName("jxbzc")
    val 教学班组成: String = "", // 20眼视光
    @SerialName("jxdd")
    val jxdd: String = "", // 85教;85教;85教;85教
    @SerialName("kcgsmc")
    val kcgsmc: String = "", // 无
    @SerialName("kch")
    val 课程ID: String = "", // 0550019
    @SerialName("kch_id")
    val kchId: String = "", // 0550019
    @SerialName("kclbmc")
    val kclbmc: String = "", // 必修课程
    @SerialName("kcmc")
    val kcmc: String = "", // 眼科学基础
    @SerialName("kcxzmc")
    val kcxzmc: String = "", // 专业课
    @SerialName("khfsmc")
    val khfsmc: String = "", // 考试
    @SerialName("kkbm_id")
    val kkbmId: String = "", // 05
    @SerialName("kklxmc")
    val kklxmc: String = "", // 主修课程
    @SerialName("kkxy")
    val kkxy: String = "", // xxxx学院
    @SerialName("ksfsmc")
    val ksfsmc: String = "", // 笔试
    @SerialName("ksxsmc")
    val ksxsmc: String = "", // 统一
    @SerialName("listnav")
    val listnav: String = "", // false
    @SerialName("localeKey")
    val localeKey: String = "", // zh_CN
    @SerialName("lrjsxx")
    val lrjsxx: String = "", // 9199401021/黄焱(医技)
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
    val sksj: String = "", // 星期一第3-5节{1-2周,4-5周};星期二第3-5节{1-5周};星期四第6-8节{7-9周};星期五第3-5节{1-2周}
    @SerialName("totalResult")
    val totalResult: String = "", // 10
    @SerialName("xf")
    val xf: String = "", // 3.0
    @SerialName("xiaoqmc")
    val xiaoqmc: String = "", // 旗山校区
    @SerialName("xnm")
    val 学年名: String = "", // 2022
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
    val zhxs: String = "", // 4
    @SerialName("zxs")
    val zxs: String = "", // 72
    @SerialName("zxxs")
    val zxxs: Int = 0 // 0
) {
    fun asTeachingClass(学年: Int, 学期: Semester) = TeachingClass(
        教学班ID = 教学班ID,
        教学班名称 = 教学班名称,
        课程ID = 课程ID,
        学年 = 学年,
        学期 = 学期,
        教学班组成=教学班组成
    )
}