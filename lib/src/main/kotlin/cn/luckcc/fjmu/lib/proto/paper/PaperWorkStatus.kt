package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperWorkStatus(
    @SerialName("funtureNum")
    val funtureNum: Int = 0, // 37
    @SerialName("submitedNum")
    val submitedNum: Int = 0, // 6
    @SerialName("theoryNum")
    val theoryNum: Int = 0, // 6
    @SerialName("unfinishedNum")
    val unfinishedNum: Int = 0 // 0
)