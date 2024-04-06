@file:Suppress("NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

abstract class UsernameTable<Entity>(name:String): BaseTable<Entity>(name) {
    final val username=varchar("username",15)
    override val primaryKey=PrimaryKey(username)
    inline fun username(username:String)=this.username eq username

    final override fun toEntity(r: ResultRow): Entity {
        val username=r[username]
        return toEntity(username, r)
    }
    abstract fun toEntity(username:String,r:ResultRow):Entity
    inline fun <T> selectNewData(
        username: String,
        column: Column<T>,
        data: List<Entity>,
        crossinline transformer: (Entity) -> T
    )=selectNewData(column,data,username(username),transformer)
    inline fun <T1,T2>selectNewData(
        username: String,
        col1: Column<T1>,
        col2: Column<T2>,
        data: List<Entity>,

        crossinline transformer: (Entity) -> Pair<T1, T2>
    )=selectNewData(col1,col2,data,username(username),transformer)
    inline fun <T1,T2,T3>selectNewData(
        username: String,
        col1:Column<T1>,
        col2:Column<T2>,
        col3:Column<T3>,
        data: List<Entity>,
        crossinline transformer:(Entity)->Triple<T1,T2,T3>
    )=selectNewData(col1,col2,col3,data,username(username),transformer)
    fun removeUserData(username: String)=deleteWhere {
        username(username)
    }>0
}
