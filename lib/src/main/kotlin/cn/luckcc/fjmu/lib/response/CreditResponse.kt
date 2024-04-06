package cn.luckcc.fjmu.lib.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.Credit
fun creditDetailUrl(token:String)="https://jxzhpt.webvpn.fjmu.edu.cn/university-facade/common/sql/private_score?token=$token"

@Serializable
data class CreditResponse(
    @SerialName("data")
    val `data`: List<Credit>,
    @SerialName("message")
    val message: String, // 成功
    @SerialName("state")
    val state: Int // 200
)