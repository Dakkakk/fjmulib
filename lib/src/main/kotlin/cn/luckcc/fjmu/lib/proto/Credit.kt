@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credit(
    @SerialName("KCMC")
    var 课程名称: String,
    @SerialName("KCDM")
    var 课程ID: String,
    @SerialName("KCGS")
    var 课程归属: String="",
    @SerialName("KCXZ")
    var 课程性质: String="",
    /**
     * 计划内课程/任意选修课
     */
    @SerialName("KIND")
    var 类型: String,
    @SerialName("JD")
    var 绩点: Float=0.0f,
    @SerialName("PSCJ")
    var 平时成绩: Float=-1f,
    @SerialName("QMCJ")
    var 期末成绩: Float=-1f,
    @SerialName("XF")
    var 学分: Float=0f,
    /**
     * 在整个大学期间是第几个学期
     */
    @SerialName("XNXQ")
    var 学期: Int=0,
    @SerialName("ZGCJ")
    var 成绩: Float=-1f,
    /**
     * 最终成绩，可能为‘合格’
     */
    @SerialName("ZZCJ")
    var 最终成绩: String="",
):Comparable<Credit> {
    override fun compareTo(other: Credit): Int {
        return if (学期==other.学期){
            课程ID.compareTo(other.课程ID)
        }else
            学期.compareTo(other.学期)
    }
    override fun toString(): String {
        return "Credit(第${学期}学期-${课程名称}($课程ID)${学分}分,${最终成绩}:$绩点(平时:${平时成绩}期末:${期末成绩}/$成绩)归属=$课程归属,性质=$课程性质,类型=$类型)"
    }
}