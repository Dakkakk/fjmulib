@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import org.jsoup.Jsoup
import cn.luckcc.fjmu.lib.FormData
import cn.luckcc.fjmu.lib.Semester
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.course.TeachingClass

fun recommendTimetableListUrl(username: String)
="https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/kbdy/bjkbdy_cxBjkbdyIndex.html?gnmkdm=N214505&layout=default&su=$username"
fun recommendTimetableUrl(username:String)
="https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/kbdy/bjkbdy_cxBjKb.html?gnmkdm=N214505&su=$username"
fun makeTimetableRequestBody(body:String,schoolYear: Int,semester: Semester): FormData {
    val map= FormData()
    val document= Jsoup.parse(body)
    val selects=document.select("select")
    selects.forEach { element ->
        element?.run {
            val id=id()
            val option=
                selectFirst("option[selected=selected]")?:selectFirst("option")?:return@run
            val value=option.`val`()
            map[id]=value
        }
    }
    map["xnm"]=schoolYear.toString()
    map["xqm"]=semester.`val`
    map["tjkbzdm"]="1"
    map["tjkbzxsdm"]="0"
    map["zxszjjs"]="false"
    map["kzlx"]="ck"
    return map
}

@Serializable
data class RecommendTableResponse(
    @SerialName("kbList")
    val kbList: List<Kb> = listOf(),
    @SerialName("kblx")
    val kblx: Int = 0, // 7
    @SerialName("sfxsd")
    val sfxsd: String = "", // 1
)

@Serializable
data class Kb(
    @SerialName("cd_id")
    val cdId: String = "", // JCXT10
    @SerialName("cdmc")
    val cdmc: String = "", // 3#南404
    @SerialName("date")
    val date: String = "", // 二○二二年八月十五日
    @SerialName("dateDigit")
    val dateDigit: String = "", // 2022年8月15日
    @SerialName("dateDigitSeparator")
    val dateDigitSeparator: String = "", // 2022-8-15
    @SerialName("day")
    val day: String = "", // 15
    @SerialName("jc")
    val jc: String = "", // 6-9节
    @SerialName("jcor")
    val jcor: String = "", // 6-9
    @SerialName("jcs")
    val jcs: String = "", // 6-9
    @SerialName("jgh_id")
    val jghId: String = "", // 9200101015
    @SerialName("jgpxzd")
    val jgpxzd: String = "", // 1
    @SerialName("jxb_id")
    val teachingClassId: String = "", // E3428D7DE7B71E03E0530100007F13E6
    @SerialName("jxbmc")
    val teachingClassName: String = "", // 病原生物学-0008A
    @SerialName("jxbrs")
    val jxbrs: String = "", // 24
    @SerialName("jxbzc")
    val teachingClassComposition: String = "", // 21眼视光第01,02,03组
    @SerialName("kch")
    val courseId: String = "", // 0104050
    @SerialName("kcmc")
    val courseName: String = "", // 病原生物学
    @SerialName("kcxszc")
    val classHourComposition: String = "", // 讲课:38,实验:18
    @SerialName("kcxzjc")
    val simpleNameOfCourseNature: String = "", // 无
    @SerialName("khfsmc")
    val examinationType: String = "", // 未安排
    @SerialName("kkzt")
    val kkzt: String = "", // 1
    @SerialName("listnav")
    val listnav: String = "", // false
    @SerialName("localeKey")
    val localeKey: String = "", // zh_CN
    @SerialName("month")
    val month: String = "", // 8
    @SerialName("oldjc")
    val oldjc: String = "", // 480
    @SerialName("oldzc")
    val oldzc: String = "", // 22272
    @SerialName("pageTotal")
    val pageTotal: Int = 0, // 0
    @SerialName("pageable")
    val pageable: Boolean = false, // true
    @SerialName("pkbj")
    val pkbj: String = "", // 1
    @SerialName("rangeable")
    val rangeable: Boolean = false, // true
    @SerialName("rsdzjs")
    val rsdzjs: Int = 0, // 0
    @SerialName("skfsmc")
    val skfsmc: String = "", // 面授讲课
    @SerialName("totalResult")
    val totalResult: String = "", // 0
    @SerialName("xf")
    val credit: String = "", // 3.5
    @SerialName("xkbz")
    val xkbz: String = "", // 无
    @SerialName("xkrs")
    val xkrs: String = "", // 0
    @SerialName("xm")
    val xm: String = "", // 李能
    @SerialName("xnm")
    val xnm: String = "", // 2022
    @SerialName("xqdm")
    val xqdm: String = "", // 0
    @SerialName("xqh1")
    val xqh1: String = "", // A,
    @SerialName("xqh_id")
    val xqhId: String = "", // A
    @SerialName("xqj")
    val xqj: String = "", // 1
    @SerialName("xqjmc")
    val xqjmc: String = "", // 星期一
    @SerialName("xqm")
    val xqm: String = "", // 3
    @SerialName("xqmc")
    val xqmc: String = "", // 旗山校区
    @SerialName("xsdj")
    val xsdj: String = "", // 6-6,7-7,8-8,9-9
    @SerialName("xslxbj")
    val xslxbj: String = "", // ◇
    @SerialName("year")
    val year: String = "", // 2022
    @SerialName("zcd")
    val zcd: String = "", // 9-11周,13-15周(单)
    @SerialName("zcmc")
    val zcmc: String = "", // 副教授
    @SerialName("zfjmc")
    val zfjmc: String = "", // 主讲
    @SerialName("zhxs")
    val zhxs: String = "", // 3
    @SerialName("zxs")
    val zxs: String = "", // 54
    @SerialName("zxxx")
    val zxxx: String = "", // 无
    @SerialName("zyfx_id")
    val zyfxId: String = "", // wfx
    @SerialName("zyfxmc")
    val zyfxmc: String = "", // 无方向
    @SerialName("zzrl")
    val zzrl: String = "" // 24
){
    fun asTeachingClass(学年:Int,学期: Semester)
    = TeachingClass(
        教学班ID = teachingClassId,
        教学班名称 = teachingClassName,
        课程ID = courseId,
        学年 = 学年,
        教学班组成 = teachingClassComposition,
        学期 = 学期
    )
}