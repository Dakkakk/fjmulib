package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageType(
    @SerialName("count")
    var count: Int = 0, // 3
    @SerialName("type")
    var type: String = "", // 其他服务
    @SerialName("typeId")
    var typeId: Int = 0, // 1012
    @SerialName("unReadSum")
    var unReadSum: Int = 0 // 0
)