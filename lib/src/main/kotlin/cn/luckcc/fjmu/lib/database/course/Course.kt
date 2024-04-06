@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.database.UsernameScheduleTable
import cn.luckcc.fjmu.lib.proto.course.Course

object Course : UsernameScheduleTable<Course>("course") {
    val 课程ID = text("courseId")
    val 课程名称 = text("courseName")
    val assessment = text("assessment")
    val examination = text("examination")
    val examinationMethod = text("examMethod")
    val type = text("type")
    val 课程类型 = text("type1")
    val 课程性质 = text("nature")
    val type2 = text("type2")
    val 开课学院 = text("facultyName")
    val 学分 = text("credit")
    val index = integer("colorSlotId")
    override fun toEntity(username: String, 学年: Int, 学期: Semester, r: ResultRow): Course {
        val course= Course(
            学年 = 学年,
            学期 = 学期,
            课程ID = r[课程ID],
            课程名称 = r[课程名称],
            assessment = r[assessment],
            examination = r[examination],
            examinationMethod = r[examinationMethod],
            type = r[type],
            课程类型 = r[课程类型],
            课程性质 = r[课程性质],
            type2 = r[type2],
            开课学院 = r[开课学院],
            学分 = r[学分],
            index = r[index]
        )
        return course
    }

    override val primaryKey = PrimaryKey(username, 学年, 学期, 课程ID, 课程名称)
}