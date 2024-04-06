@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditOverview(
    @SerialName("KCXZ")var 绩点分类名称:String,
    @SerialName("XFYQ")var 要求学分:Float=0f,
    @SerialName("YHZXF")var 已获学分:Float=0f,
):Comparable<CreditOverview>{
    inline val 未获学分:Float
        get() = 要求学分-已获学分
    override fun compareTo(other: CreditOverview): Int {
        return 绩点分类名称.compareTo(other.绩点分类名称)
    }

    override fun toString(): String {
        return "CreditOverview($绩点分类名称 $已获学分/$要求学分)"
    }

}
