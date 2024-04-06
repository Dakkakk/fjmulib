package cn.luckcc.fjmu.lib.proto.paper

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import zx.dkk.utils.time.epochMilli
import java.time.LocalDateTime

@Serializable
class SimplePaper(
    private val paperObj: PaperObj,
    private val paperParam: PaperParam,
    private val paperContent: PaperContent
) {
    val questions: List<PaperQuestion>
    val basicParams
        get() : String = paperParam.basicParams
    val commentator
        get()  = paperParam.commentator
    val paperId
        get()  = paperObj.paperId
    val param1
        get() = paperParam.param1
    val param2
        get()  = paperParam.param2
    val param3
        get()  = paperParam.param3
    val param4
        get()  = paperParam.param4
    val courseName
        get()  = paperObj.courseName
    val teacherName
        get()  = paperObj.teacherName
    val teacherId
        get()  = paperObj.teacherId
    val title
        get()  = paperObj.title
    val verificationSrc = "$commentator$basicParams$param1$param2$param3${param4}murp2020"

    init {
        questions = paperContent.paperSubjectList.map {
            PaperQuestion(
                isNeed = it.isNeed == 1,
                totalScore = it.score,
                subjectId = it.subjectId,
                createTimeMilli = it.subject.createTime,
                shortName = it.subject.shortName,
                name = it.subject.title,
            )
        }.filter {
            it.isNeed
        }
    }

    fun getFirstUnFinishedQuestion(): PaperQuestion? {
        return questions.firstOrNull {
            it.isNeed && it.answerScore == null
        }
    }

    fun toResultJson(verification: String, deviceId: String, isSave: Boolean): String {
        val element = buildJsonObject {
            put("basicParams", basicParams)
            put("commentator", commentator)
            put("deviceId", deviceId)
            put("param1", param1)
            put("param2", param2)
            put("param3", param3)
            put("param4", param4)
            putJsonObject("answers") {
                for (question in questions) {
                    putJsonObject("s${question.subjectId}") {
                        put("result", (question.answerScore!! / question.totalScore))
                    }
                }
            }
            put("signature", "")
            if (isSave)
                put("status", 0)
            else
                put("status", "1")
            put("verification", verification)
        }
        return element.toString()
    }
}
@Serializable
class PaperQuestion(
    val isNeed: Boolean,
    val totalScore: Double,
    val subjectId: String,
    val createTimeMilli: Long,
    val shortName: String,
    val name: String,
    var answerScore: Double? = null
){
    val createTime:LocalDateTime
        get() = createTimeMilli.epochMilli
}
