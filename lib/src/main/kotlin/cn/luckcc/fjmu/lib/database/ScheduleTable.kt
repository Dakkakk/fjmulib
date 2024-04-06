package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import cn.luckcc.fjmu.lib.Semester
interface IScheduleTable {
    val 学年:Column<Int>
    val 学期:Column<Semester>

    fun schoolYear(schoolYear:Int)=学年 eq schoolYear
    fun semester(semester: Semester)=学期 eq 学期

    val ResultRow.schoolYear:Int
        get() = this[学年]
    val ResultRow.semester: Semester
        get() = this[学期]

    fun atSemester(schoolYear: Int,semester: Semester)=(学年 eq schoolYear)and (学期 eq semester)
}
interface IUsernameTable{
    val username:Column<String>
    fun username(username:String)=this.username eq username

    val ResultRow.username:String
        get() = this[this@IUsernameTable.username]
    fun removeUserData(username: String):Boolean
}
interface IUsernameScheduleTable: IScheduleTable, IUsernameTable {
    fun op(username: String,schoolYear: Int,semester: Semester)=username(username)and (atSemester(schoolYear, semester))
}
