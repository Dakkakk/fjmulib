@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.database.BaseTable
import cn.luckcc.fjmu.lib.proto.course.Teacher

object Teacher: BaseTable<Teacher>("teacher") {
    val 教师ID=text("id")
    val 姓名=varchar("name",30)
    val 部门名称=varchar("faculty",30)
    val 机构=varchar("institute",30)
    override fun toEntity(r: ResultRow): Teacher {
        return Teacher(
            教工号 = r[教师ID],
            姓名 = r[姓名],
            部门名称 = r[部门名称],
            机构 = r[机构]
        )
    }

    override val primaryKey=PrimaryKey(部门名称, 教师ID)
}