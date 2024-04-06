@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CETResult(
    @SerialName("CJ")
    var 成绩: Float = 0f, // 554
    @SerialName("DJKSMC")
    var 考试名称: String = "", // 英语四级
    @SerialName("TLCJ")
    var 听力成绩: Float=0f, // 161
    @SerialName("XH")
    var 学号: String = "", // 32005000000
    @SerialName("XM")
    var 姓名: String = "", // xxx
    @SerialName("XN")
    var 学年: String = "", // 2020-2021
    @SerialName("XQ")
    var 学期: String = "", // 2
    @SerialName("XZCJ")
    var 写作成绩: Float=0f, // 87
    @SerialName("YDCJ")
    var 阅读成绩: Float=0f // 144
){

    override fun toString(): String {
        return "CETResult(${姓名}${学号}${考试名称}${学年}第${学期}学期, $成绩(听力:$听力成绩 阅读:$阅读成绩 写作:$写作成绩))"
    }
}