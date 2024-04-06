package cn.luckcc.fjmu.lib.proto.paper


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerialName("countLimit")
    val countLimit: Int? = null, // 1
    @SerialName("createTime")
    val createTime: Long = 0, // 1634784036000
    @SerialName("creator")
    val creator: String = "", // jxzlglk
    @SerialName("delete")
    val delete: Boolean = false, // false
    @SerialName("edit")
    val edit: Boolean = false, // false
    @SerialName("id")
    val id: String = "", // 12410009020f4192a8b32d59e65f7b80
    @SerialName("need")
    val need: Boolean = false, // true
    @SerialName("share")
    val share: Boolean = false, // false
    @SerialName("shortName")
    val shortName: String = "", // 重视度2021
    @SerialName("sizeLimit")
    val sizeLimit: Int? = null, // 2
    @SerialName("title")
    val title: String = "", // 重视度：你能感受到老师对你的学习收获和自身成长的关心吗？
    @SerialName("type")
    val type: String? = null, // 3
    @SerialName("typeLimit")
    val typeLimit: String? = null // image/*
)