@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import cn.luckcc.fjmu.lib.proto.Attachment

object BulletinAttachment: BaseTable<Attachment>("bulletinAttachment") {
    val newsId=reference(
        "newsId",
        Bulletin.newsId,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val 附件名称=text("name")
    val 附件下载链接=text("link")
    override fun toEntity(r: ResultRow): Attachment {
        return Attachment(
            附件名称 = r[附件名称],
            附件下载链接 = r[附件下载链接]
        )
    }

    override val primaryKey=PrimaryKey(newsId, 附件名称)
}