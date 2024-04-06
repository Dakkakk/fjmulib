package cn.luckcc.fjmu.lib.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.paper.PaperObj

fun getAllUnFinishedPapersUrl(token:String):String{
    return "https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/pj/all/list?token=$token"
}
@Serializable
data class GetAllPapersResponse(
    @SerialName("data")
    val `data`: List<PaperObj> = listOf(),
    @SerialName("message")
    val message: String = "", // 请求成功！
    @SerialName("state")
    val state: Int = 0 // 200
)