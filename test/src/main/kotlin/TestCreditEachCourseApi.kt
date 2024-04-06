import io.ktor.util.*
import java.awt.Image
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO

suspend fun testCreditEachCourseApi()= testApi { db ->


    getCreditEachCourse()?.let { credits ->
        db.addCredits(username, credits) { changes ->
            if (changes.isEmpty()) {
                println("nothing changed in credit")
            } else {
                changes.forEach { (old, new) ->
                    println("change  item->")
                    println(old)
                    println(new)
                }
            }
        }
        db.getCredits(username).forEach {
            println(it)
        }
    }
}