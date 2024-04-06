import io.ktor.utils.io.jvm.javaio.*
import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.nowDate
import java.io.File

suspend fun testBulletinApi()= testApi { db ->
    getBulletins(dayCount = 25)?.let { bulletins ->
        db.addBulletins(bulletins) {
            if (it.isEmpty()) {
                println("no bulletin added")
            } else {
                for (bulletin in it) {
                    println("add->$bulletin")
                    for (attachment in bulletin) {
                        println("attach=$attachment")
                    }
                }
            }
        }
        println("----------------------------------------")
        val path = File("D:\\d")
        db.getBulletins(nowDate.minusDays(15).epochDay, nowDate.epochDay).let {
            for (bulletin in it) {
                println("$bulletin")
                for (attachment in bulletin) {
                    attachment.附件下载链接.substringAfterLast('.')
                    println("attach=$attachment")
                    getBulletinAttachment(attachment) { suggestedName, suggestedExtension ->
                        copyTo(File(path, "$suggestedName.$suggestedExtension").outputStream())
                        println("download $suggestedName.$suggestedExtension")
                    }
                }
            }
        }
    }
}