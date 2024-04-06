@file:Suppress("NonAsciiCharacters", "NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import cn.luckcc.fjmu.lib.Semester

abstract class UsernameScheduleTable<Entity>(tableName:String): UsernameTable<Entity>(tableName) {
    val 学年=integer("schoolYear")
    val 学期=enumeration<Semester>("semester")
    inline fun schoolYear(学年: Int)=this.学年 eq 学年
    inline fun semester(学期: Semester)=this.学期 eq 学期

    inline fun atSemester(学年: Int,学期: Semester)=(this.学年 eq 学年)and (this.学期 eq 学期)
    inline fun op(username: String,学年: Int,学期: Semester)=username(username)and atSemester(学年, 学期)
    inline fun <T>selectNewData(
        username: String,
        学年: Int,
        学期: Semester,
        column: Column<T>,
        data:List<Entity>,
        crossinline transformer:(Entity)->T
    )=selectNewData(column,data,op(username,学年,学期),transformer)
    final override fun toEntity(username: String, r: ResultRow): Entity {
        val schoolYear=r[学年]
        val semester=r[学期]
        return toEntity(username,schoolYear,semester,r)
    }
    abstract fun toEntity(username: String, 学年:Int, 学期: Semester, r:ResultRow):Entity
}