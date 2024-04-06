package cn.luckcc.fjmu.lib.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.MessageType

fun messageTypeUrl()
    ="https://ehall.webvpn.fjmu.edu.cn/jsonp/getUserTags"
@Serializable
data class MessageTypeResponse(
    @SerialName("contextPath")
    val contextPath: String = "",
    @SerialName("data")
    val `data`: UserMessage = UserMessage(),
    @SerialName("hasLogin")
    val hasLogin: Boolean = false, // true
    @SerialName("message")
    val message: String = "",
    @SerialName("result")
    val result: String = "" // success
)

@Serializable
data class UserMessage(
    @SerialName("allUnReadSum")
    val allUnReadSum: Int = 0, // 0
    @SerialName("typeList")
    val messageTypeList: List<MessageType> = listOf()
)

