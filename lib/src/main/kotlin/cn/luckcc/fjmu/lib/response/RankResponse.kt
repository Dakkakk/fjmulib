package cn.luckcc.fjmu.lib.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.Rank

fun getRankUrl(token: String): String =
    "https://jxzhpt.webvpn.fjmu.edu.cn/university-facade/common/sql/private_score_top?token=$token"

@Serializable
data class RankResponse(
    @SerialName("data")
    val `data`: List<Rank> = listOf(),
    @SerialName("message")
    val message: String = "", // 成功
    @SerialName("state")
    val state: Int = 0 // 200
)