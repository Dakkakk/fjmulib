@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zx.dkk.utils.time.epochMilli
import zx.dkk.utils.time.localizedText

@Serializable
data class Message(
    var 内容: String = "", // 您借阅的【病理学习题集】快到期了,请速至图书馆办理续借
    var 消息ID: Int = 0, // 1647985
//    @SerialName("mobileUrl")
//    val mobileUrl: Any = Any(), // null
//    @SerialName("pcUrl")
//    val pcUrl: Any = Any(), // null
    var 已读: Boolean = false, // true
//    @SerialName("senderId")
//    val senderId: Any = Any(), // null
//    @SerialName("senderName")
//    val senderName: Any = Any(), // null
    var epochMilli: Long, // 2022-06-23 08:53:47
    var 标题: String = "", // 书籍逾期提醒
    var 消息类型: String = "", // 其他服务
    var 消息类型ID: Int = 0, // 1012
//    @SerialName("urlDesc")
//    val urlDesc: Any = Any() // null
):Comparable<Message>{
    override fun compareTo(other: Message): Int {
        return if (epochMilli==other.epochMilli){
            if (消息类型==other.消息类型){
                消息ID.compareTo(other.消息ID)
            }else
                消息类型.compareTo(other.消息类型)
        }else
            epochMilli.compareTo(other.epochMilli)
    }
    @Transient
    val dateTime=epochMilli.epochMilli
    override fun toString(): String {
        return "Message(内容='$内容', 消息ID=$消息ID, 已读=$已读, dateTime=${dateTime.localizedText}, 标题='$标题', 消息类型='$消息类型', 消息类型ID=$消息类型ID)"
    }
}
