@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.proto.Credit
object Credit: UsernameTable<Credit>("credit") {
    val 课程名称=text("courseName")
    val 课程ID=text("courseId")
    val 课程归属=text("type")
    val 课程性质=text("nature")
    val 类型=text("kind")
    val 绩点=float("credit")
    val 平时成绩=float("ordinalResult")
    val 期末成绩=float("finalResult")
    val 学分=float("score")
    /**
     * 在整个大学期间是第几个学期
     */
    val 学期=integer("semester")
    val 成绩=float("result")
    /**
     * 最终成绩，可能为‘合格’
     */
    val 最终成绩=text("fResult")
    override val primaryKey=PrimaryKey(username, 学期, 课程ID)
    override fun toEntity(username: String, r: ResultRow): Credit {
        return Credit(
            课程名称 = r[课程名称],
            课程ID = r[课程ID],
            课程归属 = r[课程归属],
            课程性质 = r[课程性质],
            类型 = r[类型],
            绩点 = r[绩点],
            平时成绩 = r[平时成绩],
            期末成绩 = r[期末成绩],
            学分 = r[学分],
            学期=r[学期],
            成绩 = r[成绩],
            最终成绩 = r[最终成绩]
        )
    }
}