@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.proto.Rank

object Rank : UsernameTable<Rank>("rank") {
    val isSelf = bool("isSelf")
    val 班级排名 = integer("rank")
    val 平均绩点 = float("credit")
    val 姓名 = varchar("name", 30)
    override val primaryKey = PrimaryKey(username, 班级排名)
    override fun toEntity(username: String, r: ResultRow): Rank {
        return Rank(
            isSelf = r[isSelf],
            班级排名 = r[班级排名],
            平均绩点 = r[平均绩点],
            姓名 = r[姓名]
        )
    }
}