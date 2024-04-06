package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.paper.PaperWorkStatus
fun getPaperWorkStatusUrl(token:String)
="https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/pj/statsForPaper?token=$token"
@Serializable
data class PaperWorkStatusResponse(
    @SerialName("data")
    val paperWorkStatus: PaperWorkStatus = PaperWorkStatus(),
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)