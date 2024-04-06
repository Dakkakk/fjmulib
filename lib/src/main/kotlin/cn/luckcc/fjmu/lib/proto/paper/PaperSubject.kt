package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperSubject(
    @SerialName("isneed")
    val isNeed: Int = 0, // 1
    @SerialName("score")
    val score: Double = 0.0, // 20.0
    @SerialName("sort")
    val sort: Int = 0, // 1
    @SerialName("subject")
    val subject: Subject = Subject(),
    @SerialName("subjectId")
    val subjectId: String = "", // 12410009020f4192a8b32d59e65f7b80
    @SerialName("type")
    val type: Int = 0 // 4
)