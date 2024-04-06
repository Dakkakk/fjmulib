package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment internal constructor(
    @SerialName("name")
    val 附件名称: String,
    @SerialName("link")
    val 附件下载链接: String
)