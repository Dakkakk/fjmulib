@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Rank(
    @SerialName("ISME")
    private var self:Int,
    @SerialName("RANK")
    var 班级排名:Int,
    @SerialName("SCORE")
    var 平均绩点:Float,
    @SerialName("XM")
    var 姓名:String,
): Comparable<Rank>{
    constructor(isSelf: Boolean,班级排名: Int,平均绩点: Float,姓名: String):this(if (isSelf)1 else 0,班级排名, 平均绩点, 姓名)
    override fun compareTo(other: Rank): Int {
        return 班级排名.compareTo(other.班级排名)
    }
    val isSelf:Boolean
        get() = self==1
}
