package cn.luckcc.fjmu.lib.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.proto.SimpleStudentInfo
fun simpleStudentInfoUrl(token:String)
="https://jxzhpt.webvpn.fjmu.edu.cn/university-facade/common/sql/state?isquit=1&token=$token"
@Serializable
data class StudentInfoSimpleResponse(
    @SerialName("data")
    val `data`: List<SimpleStudentInfo> = listOf(),
    @SerialName("message")
    val message: String = "", // 成功
    @SerialName("state")
    val state: Int = 0 // 200
)