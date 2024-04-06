package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.MessageType
import zx.dkk.utils.encodeURIComponent

fun messageResponse(messageType: MessageType, size:Int)
    ="https://ehall.webvpn.fjmu.edu.cn/jsonp/getTagsMessages?typeId=${messageType.typeId}&size=$size&start=0&typeName=${encodeURIComponent(messageType.type)}"
@Serializable
data class MessageResponse(
    @SerialName("contextPath")
    val contextPath: String = "",
    @SerialName("data")
    val `data`: MessageTotal = MessageTotal(),
    @SerialName("hasLogin")
    val hasLogin: Boolean = false, // true
    @SerialName("message")
    val message: String = "",
    @SerialName("result")
    val result: String = "" // success
)

@Serializable
data class MessageTotal(
    @SerialName("children")
    val children: List<MessageData> = listOf(),
    @SerialName("count")
    val count: Int = 0, // 3
    @SerialName("type")
    val type: String = "", // 其他服务
    @SerialName("typeId")
    val typeId: Int = 0 // 1012
)

@Serializable
data class MessageData(
    @SerialName("content")
    val content: String = "", // 您借阅的【病理学习题集】快到期了,请速至图书馆办理续借
    @SerialName("id")
    val id: Int = 0, // 1647985
//    @SerialName("mobileUrl")
//    val mobileUrl: Any = Any(), // null
//    @SerialName("pcUrl")
//    val pcUrl: Any = Any(), // null
    @SerialName("read")
    val isReadied: Boolean = false, // true
//    @SerialName("senderId")
//    val senderId: Any = Any(), // null
//    @SerialName("senderName")
//    val senderName: Any = Any(), // null
    @SerialName("time")
    val time: String? = null, // 2022-06-23 08:53:47
    @SerialName("title")
    val title: String = "", // 书籍逾期提醒
    @SerialName("type")
    val type: String = "", // 其他服务
    @SerialName("typeId")
    val typeId: Int = 0, // 1012
//    @SerialName("urlDesc")
//    val urlDesc: Any = Any() // null
)