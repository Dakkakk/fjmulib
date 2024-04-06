@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.Campus
import cn.luckcc.fjmu.lib.database.BaseTable
import cn.luckcc.fjmu.lib.proto.course.Classroom

object Classroom: BaseTable<Classroom>("classroom") {
    val 教室名称=text("name")
    val 教室类型=text("type")
    val 教室ID=text("id")
    val 教学楼=text("building")
    val 校区=enumeration<Campus>("campus")
    override fun toEntity(r: ResultRow): Classroom {
        return Classroom(
            教室ID = r[教室ID],
            教室名称 = r[教室名称],
            教室类型 = r[教室类型],
            教学楼 = r[教学楼],
            校区 = r[校区]
        )
    }

    override val primaryKey=PrimaryKey(校区, 教学楼, 教室名称)
}