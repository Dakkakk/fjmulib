@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.proto.CreditOverview

object CreditOverview: UsernameTable<CreditOverview>("creditOverview") {
    val 绩点分类名称=text("name")
    val 要求学分=float("requireCredit")
    val 已获学分=float("acquiredCredit")
    override val primaryKey=PrimaryKey(username, 绩点分类名称)
    override fun toEntity(username: String, r: ResultRow): CreditOverview {
        return CreditOverview(
            绩点分类名称 = r[绩点分类名称],
            要求学分 = r[要求学分],
            已获学分 = r[已获学分]
        )
    }
}