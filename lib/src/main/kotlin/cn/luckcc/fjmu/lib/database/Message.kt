@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import cn.luckcc.fjmu.lib.proto.Message

object Message: UsernameTable<Message>("message") {
    val 标题=text("title")
    val 内容=text("content")
    val 消息类型=text("messageType")
    val 消息类型Id=integer("messageTypeId")
    val epochMilli=long("epochMilli")
    val 消息ID=integer("messageId")
    val 已读=bool("isRead")
    override val primaryKey=PrimaryKey(username, 消息类型, 消息ID)
    @Suppress("NOTHING_TO_INLINE")
    inline fun messageType(消息类型:String)= cn.luckcc.fjmu.lib.database.Message.消息类型 eq 消息类型
    override fun toEntity(username: String, r: ResultRow): Message {
        return Message(
            标题 = r[标题],
            内容 = r[内容],
            消息类型 = r[消息类型],
            消息类型ID = r[消息类型Id],
            消息ID = r[消息ID],
            epochMilli = r[epochMilli],
            已读 = r[已读]
        )
    }
}