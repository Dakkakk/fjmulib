@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import cn.luckcc.fjmu.lib.proto.Bulletin

object Bulletin: BaseTable<Bulletin>("bulletin") {
    val newsId=integer("newsId")
    val 标题=text("label")
    val 部门名称=varchar("faculty",30)
    val epochDay=long("epochDay")
    val 内容=text("content")
    val 链接=text("link")
    @Suppress("NOTHING_TO_INLINE")
    inline fun dayRange(startInclusive:Long, endInclusive:Long)= (epochDay.between(startInclusive,endInclusive))
    override fun toEntity(r: ResultRow): Bulletin {
        return Bulletin(
            newsId=r[newsId],
            标题=r[标题],
            部门名称 = r[部门名称],
            epochDay = r[epochDay],
            内容 = r[内容],
            链接 = r[链接]
        )
    }

    override val primaryKey=PrimaryKey(newsId)
}