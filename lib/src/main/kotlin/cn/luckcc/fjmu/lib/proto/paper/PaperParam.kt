package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperParam(
    @SerialName("basicParams")
    val basicParams: String = "", // EFB10377F897FC5CE0530100007F384A@7@
    @SerialName("commentator")
    val commentator: String = "", // 9202101035
    @SerialName("paperId")
    val paperId: String = "", // edae29079f5e46449d1a89ac2f4b2d63
    @SerialName("param1")
    val param1: String = "", // EFB10377F897FC5CE0530100007F384A
    @SerialName("param2")
    val param2: String = "", // 7
    @SerialName("param3")
    val param3: String = "", // 1678233600000
    @SerialName("param4")
    val param4: String = "" // 1010600
)