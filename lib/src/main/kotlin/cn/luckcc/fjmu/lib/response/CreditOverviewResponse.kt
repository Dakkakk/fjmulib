package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.CreditOverview

fun creditOverviewUrl(token:String)="https://jxzhpt.webvpn.fjmu.edu.cn/university-facade/common/sql/private_socre_total?token=$token"
@Serializable
data class CreditOverviewResponse(
    @SerialName("data")
    val `data`: List<CreditOverview> = listOf(),
    @SerialName("message")
    val message: String = "", // 成功
    @SerialName("state")
    val state: Int = 0 // 200
)