package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.*
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit
) =
    InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }
class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val builder = QueryBuilder(true)
        val values = arguments!!.first()
        val expr = if (values.isEmpty()) ""
        else with(builder) {
            values.appendTo(prefix = "VALUES (", postfix = ")") { (col, value) ->
                registerArgument(col, value)
            }
            toString()
        }
        val columns=values.map { it.first }
        val autoIncColumn=table.autoIncColumn
        val nextValExpression = autoIncColumn?.autoIncColumnType?.nextValExpression?.takeIf { autoIncColumn !in columns }
        val isInsertFromSelect = columns.isNotEmpty() && expr.isNotEmpty() && !expr.startsWith("VALUES")
        val defaultValueExpression=transaction.db.dialect.functionProvider.DEFAULT_VALUE_EXPRESSION
        val (columnsToInsert, valuesExpr) = when {
            isInsertFromSelect -> columns to expr
            nextValExpression != null && columns.isNotEmpty() -> (columns + autoIncColumn) to expr.dropLast(1) + ", $nextValExpression)"
            nextValExpression != null -> listOf(autoIncColumn) to "VALUES ($nextValExpression)"
            columns.isNotEmpty() -> columns to expr
            else -> emptyList<Column<*>>() to defaultValueExpression
        }
        val keyColumnsExpr=keys.joinToString(prefix = "KEY(", postfix = ")") { transaction.identity(it) }
        val columnsExpr = columnsToInsert.takeIf { it.isNotEmpty() }?.joinToString(prefix = "(", postfix = ")") { transaction.identity(it) } ?: ""
        return "MERGE INTO ${transaction.identity(table)} $columnsExpr $keyColumnsExpr $valuesExpr"
    }
}

/**
 * Example:
 * val items = listOf(...)
 * MyTable.batchUpsert(items) { table, item  ->
 * 	table[id] = item.id
 *	table[value1] = item.value1
 * }
 */

fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(BatchInsertStatement, E) -> Unit
) =
    BatchInsertOrUpdate(this, keys = keys).apply {
        data.forEach {
            addBatch()
            body(this, it)
        }
        execute(TransactionManager.current())
    }

class BatchInsertOrUpdate(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues = false) {
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val builder = QueryBuilder(true)
        val values = arguments!!.first()
        val expr = if (values.isEmpty()) ""
        else with(builder) {
            values.appendTo(prefix = "VALUES (", postfix = ")") { (col, value) ->
                registerArgument(col, value)
            }
            toString()
        }
        val columns=values.map { it.first }
        val autoIncColumn=table.autoIncColumn
        val nextValExpression = autoIncColumn?.autoIncColumnType?.nextValExpression?.takeIf { autoIncColumn !in columns }
        val isInsertFromSelect = columns.isNotEmpty() && expr.isNotEmpty() && !expr.startsWith("VALUES")
        val defaultValueExpression=transaction.db.dialect.functionProvider.DEFAULT_VALUE_EXPRESSION
        val (columnsToInsert, valuesExpr) = when {
            isInsertFromSelect -> columns to expr
            nextValExpression != null && columns.isNotEmpty() -> (columns + autoIncColumn) to expr.dropLast(1) + ", $nextValExpression)"
            nextValExpression != null -> listOf(autoIncColumn) to "VALUES ($nextValExpression)"
            columns.isNotEmpty() -> columns to expr
            else -> emptyList<Column<*>>() to defaultValueExpression
        }
        val keyColumnsExpr=keys.joinToString(prefix = "KEY(", postfix = ")") { transaction.identity(it) }
        val columnsExpr = columnsToInsert.takeIf { it.isNotEmpty() }?.joinToString(prefix = "(", postfix = ")") { transaction.identity(it) } ?: ""
        return "MERGE INTO ${transaction.identity(table)} $columnsExpr $keyColumnsExpr $valuesExpr"
    }
}
