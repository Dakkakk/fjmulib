package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoPaperResponse(
    @SerialName("data")
    val id: String?, // 97.6
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)
@Serializable
data class SavePaperResponse(
    @SerialName("data")
    val score: Float?, // 97.6
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)
