package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.proto.BookRequest

object BookRequest: UsernameScheduleTable<BookRequest>("bookReq") {
    val teacher=varchar("teacher",20)
    val courseNameWithId=text("courseNameWithId")
    val bookWithISBN=text("bookWithISBN")
    override val primaryKey=PrimaryKey(username,学年,学期, bookWithISBN)
    override fun toEntity(username: String, 学年: Int, 学期: Semester, r: ResultRow): BookRequest {
        return BookRequest(
            学年 = 学年,
            学期 = 学期,
            bookWithISBN = r[bookWithISBN],
            courseNameWithId = r[courseNameWithId],
            teacher = r[teacher]
        )
    }
}