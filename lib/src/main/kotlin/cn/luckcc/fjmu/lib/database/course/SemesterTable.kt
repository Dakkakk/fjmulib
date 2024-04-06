@file:Suppress("NonAsciiCharacters", "NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.database.BaseTable
import cn.luckcc.fjmu.lib.proto.course.YearSemester

object SemesterTable: BaseTable<YearSemester>("semester") {
    val 学年=integer("schoolYear")
    val 学期=enumeration<Semester>("semester")
    val start=long("start")

    inline fun schoolYear(学年: Int)= SemesterTable.学年 eq 学年
    inline fun semester(学期: Semester)= SemesterTable.学期 eq 学期

    inline fun atSemester(学年: Int,学期: Semester)=(SemesterTable.学年 eq 学年)and (SemesterTable.学期 eq 学期)
    override val primaryKey=PrimaryKey(学年, 学期)
    override fun toEntity(r: ResultRow): YearSemester {
        return YearSemester(
            schoolYear = r[学年],
            semester = r[学期],
            start = r[start]
        )
    }

    fun selectStartDay(学年: Int,学期: Semester):Long?{
        return slice(start).select(atSemester(学年, 学期)).limit(1).singleOrNull()?.get(start)
    }
}