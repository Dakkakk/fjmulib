package cn.luckcc.fjmu.lib.database.course

import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.database.UsernameTable
import cn.luckcc.fjmu.lib.proto.course.Timeline
import zx.dkk.data.decode

object Timeline: UsernameTable<List<Timeline>>("timeline") {
    val data=binary("data",1024*5)
    override fun toEntity(username: String, r: ResultRow): List<Timeline> {
        return decode(r[data])
    }
}