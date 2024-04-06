package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperContent(
    @SerialName("author")
    val author: String = "", // jxzlglk
    @SerialName("beforeSubmitType")
    val beforeSubmitType: Int = 0, // 1
    @SerialName("beginTime")
    val beginTime: Long = 0, // 1676217600000
    @SerialName("businessType")
    val businessType: String = "", // 1
    @SerialName("candidates")
    val candidates: Boolean = false, // false
    @SerialName("createTime")
    val createTime: Long = 0, // 1676859081000
    @SerialName("disorder")
    val disorder: Boolean = false, // false
    @SerialName("endTime")
    val endTime: Long = 0, // 1693497600000
    @SerialName("guideWord")
    val guideWord: String = "", // 同学，你好，感谢你参加本次的评教！       1、本次评价关乎着老师的职称评定及评优（评先进）等，请你务必带着一个大学生应有的态度，用批判的思维去公平、公正的评价教你的每个老师。      2、本次评教是“匿名”进行，你们给老师写的意见老师都会看到（但是请放心，老师看不到你们的名字），你的建议若对老师下一步改进教学有很好的指导意义，信息员会将老师的回复公布到班级群内回复你（因匿名，所以无法直接给本人），也供其他同学参考。      教务处再次感谢你对教学做出的努力和贡献！！！
    @SerialName("id")
    val id: String = "", // edae29079f5e46449d1a89ac2f4b2d63
    @SerialName("jumpP1")
    val jumpP1: String = "", // 1
    @SerialName("jumptype")
    val jumptype: String = "", // 1
    @SerialName("logic")
    val logic: String = "", // {"hideQuestions":[],"relations":[]}
    @SerialName("name")
    val name: String = "", // 理论-2022-2023第二
    @SerialName("paperSubjectList")
    val paperSubjectList: List<PaperSubject> = listOf(),
    @SerialName("ruleGroup")
    val ruleGroup: String = "", // 20e2d4a41a2346f18730b7b6499125ee
    @SerialName("showAnswer")
    val showAnswer: Int = 0, // 0
    @SerialName("showQuestionNo")
    val showQuestionNo: Int = 0, // 1
    @SerialName("showScore")
    val showScore: Int = 0, // 1
    @SerialName("status")
    val status: Boolean = false, // true
    @SerialName("title")
    val title: String = "", // 理论-2022-2023第二学期期末评教
    @SerialName("type")
    val type: String = "" // 0
)