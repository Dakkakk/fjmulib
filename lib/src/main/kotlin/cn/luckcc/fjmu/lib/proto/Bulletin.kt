@file:Suppress("NonAsciiCharacters", "NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.localizedText

@Suppress("OVERRIDE_BY_INLINE")
@Serializable
data class Bulletin(
    var newsId: Int,
    @SerialName("label")
    var 标题: String,
    @SerialName("faculty")
    var 部门名称: String,
    var epochDay: Long,
    @SerialName("content")
    var 内容: String,
    var 链接: String
) : MutableList<Attachment>, Comparable<Bulletin> {
    @Transient
    val date = epochDay.epochDay


    override fun compareTo(other: Bulletin): Int {
        return if (部门名称 == other.部门名称) {
            if (epochDay == other.epochDay)
                -newsId.compareTo(other.newsId)
            else
                -epochDay.compareTo(other.epochDay)
        } else {
            部门名称.compareTo(other.部门名称)
        }
    }

    override fun toString(): String {
        return "Bulletin(${部门名称}${date.localizedText} ${newsId}/$标题,\n$内容)"
    }
    val attachments= arrayListOf<Attachment>()

    override inline val size: Int
        get() = attachments.size

    override inline fun clear()=attachments.clear()

    override inline fun addAll(elements: Collection<Attachment>)=attachments.addAll(elements)

    override inline fun addAll(index: Int, elements: Collection<Attachment>)=attachments.addAll(index, elements)

    override inline fun add(index: Int, element: Attachment)=attachments.add(index, element)

    override inline fun add(element: Attachment)=attachments.add(element)

    override inline fun containsAll(elements: Collection<Attachment>)=attachments.containsAll(elements)

    override inline fun contains(element: Attachment)=attachments.contains(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bulletin

        if (newsId != other.newsId) return false

        return true
    }

    override inline fun get(index: Int)=attachments.get(index)

    override fun hashCode(): Int {
        return newsId
    }

    override inline fun isEmpty()=attachments.isEmpty()

    override inline fun iterator()=attachments.iterator()

    override inline fun listIterator()=attachments.listIterator()

    override inline fun listIterator(index: Int)=attachments.listIterator(index)

    override inline fun removeAt(index: Int)=attachments.removeAt(index)

    override inline fun subList(fromIndex: Int, toIndex: Int)=attachments.subList(fromIndex, toIndex)

    override inline fun set(index: Int, element: Attachment)=attachments.set(index, element)

    override inline fun retainAll(elements: Collection<Attachment>)=attachments.retainAll(elements)

    override inline fun removeAll(elements: Collection<Attachment>)=attachments.removeAll(elements)

    override inline fun remove(element: Attachment): Boolean =attachments.remove(element)

    override inline fun lastIndexOf(element: Attachment): Int =attachments.lastIndexOf(element)

    override inline fun indexOf(element: Attachment): Int =attachments.indexOf(element)
}

