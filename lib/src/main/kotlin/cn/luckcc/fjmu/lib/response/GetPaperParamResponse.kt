package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.paper.PaperObj
import cn.luckcc.fjmu.lib.proto.paper.PaperParam
fun getPaperParamResponseUrl(
    token:String,
    paper: PaperObj
)="https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/pj/jumpToPaper?token=${token}&id=${paper.id}"
@Serializable
data class GetPaperParamResponse(
    @SerialName("data")
    val paperParam: PaperParam = PaperParam(),
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)