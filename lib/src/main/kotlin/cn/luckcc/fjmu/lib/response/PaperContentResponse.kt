package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.paper.PaperContent
import cn.luckcc.fjmu.lib.proto.paper.PaperObj

fun getPaperContentResponseUrl(paperObj: PaperObj, token:String)="https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/questionnaire/paper/${paperObj.paperId}?token=$token"
@Serializable
data class PaperContentResponse(
    @SerialName("data")
    val paperContent: PaperContent = PaperContent(),
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)