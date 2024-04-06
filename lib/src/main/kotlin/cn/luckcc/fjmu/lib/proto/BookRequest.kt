package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Semester

@Serializable
data class BookRequest(
    var teacher:String,
    var courseNameWithId:String,
    var bookWithISBN:String,
    var 学年:Int,
    var 学期: Semester
)
