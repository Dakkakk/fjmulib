package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PaperObj(
    @SerialName("COURSENAME")
    val courseName: String = "", // 医患沟通
    @SerialName("ENDTIME")
    val endTime: Long = 0, // 1693497600000
    @SerialName("ID")
    val id: String = "", // f03ebb6530ed1176e4ebca9298657f35
    @SerialName("PJTIME")
    val paperTime: Long = 0, // 1678233600000
    @SerialName("PKBH")
    val pkbh: String = "", // EFB10377F897FC5CE0530100007F384A
    @SerialName("SCORE")
    val score: String?=null, // null
    @SerialName("STATUS")
    val status: Int?=null, // null
    @SerialName("TEACHERNAME")
    val teacherName: String = "", // 郑雨心
    @SerialName("TEACHERNO")
    val teacherId: String = "", // 9202101035
    @SerialName("TITLE")
    val title: String = "", // 理论-2022-2023第二学期期末评教
    @SerialName("TYPE")
    val type: Int = 0, // 7
    @SerialName("TYPENAME")
    val typeName: String = "", // 期末
    @SerialName("XSPJID")
    val paperId: String = "" // edae29079f5e46449d1a89ac2f4b2d63
)