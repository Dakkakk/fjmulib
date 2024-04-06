@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpleStudentInfo(
    @SerialName("CLASSNAME")
    val 班级名称: String, // 2020级四年制xxx
    @SerialName("COMEYEAR")
    val 入学年份: Int, // 2020
    @SerialName("DEPTNAME")
    val 所属学院: String, // xxxx学院
    @SerialName("GRADEYEAR")
    val gradeYear: Int = 0, // 2020
    @SerialName("MAJORNAME")
    val 专业名称: String = "", // 四年制xxx
    @SerialName("STUDENTNAME")
    val 姓名: String = "", // xxx
    @SerialName("STUDENTNO")
    val 学号: String = "", // 3200500000
    @SerialName("YEARLIMIT")
    val 学制: Int = 0 // 4
)