@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.proto.CETResult

object CetResult: UsernameTable<CETResult>("cetResult") {
    val 成绩=float("result")
    val 考试名称=varchar("type",10)
    val 听力成绩=float("listenResult")
    val 姓名=varchar("name",20)
    val 学年=varchar("sYear",15)
    val 学期=varchar("semester",5)
    val 写作成绩=float("writing")
    val 阅读成绩=float("reading")
    override val primaryKey=PrimaryKey(username, 学年, 学期, 考试名称)
    override fun toEntity(username: String, r: ResultRow): CETResult {
        return CETResult(
            学号 = username,
            学年 = r[学年],
            学期 = r[学期],
            考试名称 = r[考试名称],
            成绩 = r[成绩],
            听力成绩 = r[听力成绩],
            写作成绩 = r[写作成绩],
            阅读成绩 = r[阅读成绩],
            姓名 = r[姓名]
        )
    }
}