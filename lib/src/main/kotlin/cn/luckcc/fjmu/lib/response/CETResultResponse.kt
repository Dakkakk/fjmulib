package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.CETResult
fun cetResultUrl(token:String)="https://jxzhpt.webvpn.fjmu.edu.cn/university-facade/common/sql/english_four_six?token=$token"
@Serializable
data class CETResultResponse(
    @SerialName("data")
    val `data`: List<CETResult> = listOf(),
    @SerialName("message")
    val message: String = "", // 成功
    @SerialName("state")
    val state: Int = 0 // 200
)