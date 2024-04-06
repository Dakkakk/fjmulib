@file:Suppress("NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

abstract class BaseTable<Entity>(name: String) : Table(name) {
    fun toEntities(query: Query): List<Entity> {
        return query.map(::toEntity)
    }

    @JvmName("queryToEntities")
    inline fun Query.toEntities() = toEntities(this)
    inline fun Query.singleEntity(): Entity? {
        val row = singleOrNull() ?: return null
        return row.toEntity()
    }

    @JvmName("rowToEntity")
    inline fun ResultRow.toEntity() = toEntity(this)
    inline fun <T> selectNewData(
        column: Column<T>,
        data: List<Entity>,
        initialConstraint:Op<Boolean>?=null,
        crossinline transformer: (Entity) -> T
    ): List<Entity> {
        val ids=data.map(transformer)
        var op:Op<Boolean> =(column inList ids)
        if (initialConstraint!=null)
            op=(initialConstraint and op)
        val oldIds = slice(column).select(op).map { it[column] }
        return data.filter {
            transformer(it) !in oldIds
        }
    }
    inline fun <T1,T2>selectNewData(
        col1:Column<T1>,
        col2:Column<T2>,
        data: List<Entity>,
        initialConstraint:Op<Boolean>?=null,
        crossinline transformer:(Entity)->Pair<T1,T2>,
    ):List<Entity>{
        val ids=data.map(transformer)
        var op:Op<Boolean> =((col1 to col2) inList ids)
        if (initialConstraint!=null)
            op=(initialConstraint and op)
        val oldIds=slice(col1,col2).select(op).map {
            it[col1] to it[col2]
        }
        return data.filter {
            transformer(it) !in oldIds
        }
    }
    inline fun <T1,T2,T3>selectNewData(
        col1:Column<T1>,
        col2:Column<T2>,
        col3:Column<T3>,
        data: List<Entity>,
        initialConstraint:Op<Boolean>?=null,
        crossinline transformer:(Entity)->Triple<T1,T2,T3>
    ):List<Entity>{
        val ids=data.map(transformer)
        var op:Op<Boolean> =(Triple(col1,col2,col3) inList ids)
        if (initialConstraint!=null)
            op=(initialConstraint and op)
        val oldIds=slice(col1,col2,col3).select(op).map {
            Triple(it[col1],it[col2],it[col3])
        }
        return data.filter {
            transformer(it) !in oldIds
        }
    }


    abstract fun toEntity(r: ResultRow): Entity
}