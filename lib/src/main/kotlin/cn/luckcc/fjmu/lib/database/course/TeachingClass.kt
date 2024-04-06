@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.database.UsernameScheduleTable
import cn.luckcc.fjmu.lib.proto.course.TeachingClass

object TeachingClass: UsernameScheduleTable<TeachingClass>("teachingClass") {
    val 教学班ID=text("id")
    val 教学班名称=text("name")
    val 课程ID=text("courseId")
    val 教学班组成=text("composition")
    override fun toEntity(username: String, 学年: Int, 学期: Semester, r: ResultRow): TeachingClass {
        return TeachingClass(
            学年 = 学年,
            学期 = 学期,
            教学班ID = r[教学班ID],
            教学班名称 = r[教学班名称],
            课程ID = r[课程ID],
            教学班组成 = r[教学班组成]
        )
    }

    override val primaryKey=PrimaryKey(username,学年,学期, 教学班ID, 教学班名称)
}