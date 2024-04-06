@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Campus
import cn.luckcc.fjmu.lib.proto.FreeClassroom
import zx.dkk.utils.collections.NONE
import zx.dkk.utils.encodeURIComponent
import java.time.DayOfWeek

fun freeClassroomUrl(campus: Campus, weekOfSemester:Int, dayOfWeek:DayOfWeek, 节次:Int) =
    "https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/common/sql/free_class_rooms?XQMC=${
        encodeURIComponent(campus.`val`)
    }&CDLB=&JXLMC=&zc=${weekOfSemester}&xqj=${dayOfWeek.value}&jc=${节次}"

@Serializable
data class FreeClassroomResponse(
    @SerialName("data")
    val `data`: List<FreeClassroom> = listOf(),
    @SerialName("message")
    val message: String = NONE,
    @SerialName("state")
    val state: Int = 0
)