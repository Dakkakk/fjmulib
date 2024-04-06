import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import zx.dkk.config.Config
import cn.luckcc.fjmu.lib.ScheduleClient
import cn.luckcc.fjmu.lib.database.ScheduleDatabase
import cn.luckcc.fjmu.lib.def.Default
import cn.luckcc.fjmu.lib.def.password
import cn.luckcc.fjmu.lib.def.username
import cn.luckcc.fjmu.lib.useVpnProxy
import io.ktor.util.*
import kotlinx.coroutines.*
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

val config by lazy{Config(File("./test.config").toPath())}
val client by lazy {
    ScheduleClient(username, password,false)
}

val db by lazy {
    ScheduleDatabase(File("./schedule.mv.db").toPath())
}
val String.md5: String
    get() {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}
suspend fun main()= application{
    useVpnProxy=false
    // apply your username and password here
    Default.apply("username","password")
    Window(onCloseRequest = ::exitApplication, title = "TestFjmuLib"){
        MaterialTheme {
            var showImgCodeBar by remember {
                mutableStateOf(false)
            }
            var imgCodeResult:String? by remember {
                mutableStateOf(null)
            }
            val coroutineScope= rememberCoroutineScope()
            LaunchedEffect(Unit){
                launch(Dispatchers.IO){
                    Default.initScheduleField(config)
                    println(client.checkPasswordAccuracy(null))
                    client.imgCodeGetter={
                        showImgCodeBar=true
                        while (true){
                            if (imgCodeResult!=null)
                                break
                            delay(200)
                        }
                        val code=imgCodeResult!!
                        code
                    }
//                    testCourseApi()

                }
            }
            if (showImgCodeBar){
                Column(
                    modifier = Modifier
                        .size(120.dp,200.dp)
                ){
                    var img:ImageBitmap? by remember {
                        mutableStateOf(null)
                    }
                    var code by remember {
                        mutableStateOf("")
                    }
                    fun loadImg(){
                        coroutineScope.launch(Dispatchers.IO){
                            val byteArray=client.imgCode()?.toByteArray()?:return@launch
                            val image=ImageIO.read(byteArray.inputStream())
                            img=image.toComposeImageBitmap()
                        }
                    }
                    LaunchedEffect(Unit){
                        loadImg()
                    }
                    Card(
                        modifier = Modifier
                            .size(120.dp,40.dp),
                        onClick = ::loadImg
                    ){
                        img?.let {img->
                            Image(
                                bitmap = img,
                                contentDescription = "imgCode",
                            )
                        }
                    }
                    TextField(
                        value = code,
                        onValueChange = {new->
                            code=new
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ElevatedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            imgCodeResult=code
                            showImgCodeBar=false
                        }
                    ){
                        Text("Send")
                    }
                }
            }
        }
    }
}
val scores= doubleArrayOf(
    0.95,.965,.97,.98,.995,//19
    0.9,0.915,0.92,.93,.945,//18
)
suspend fun testDoPaperApi()= testApi { db ->
    val status = getPaperWorkStatus()
    println(status)
}

suspend inline fun testApi(
    crossinline block: suspend ScheduleClient.(db: ScheduleDatabase) -> Unit
) {
    client.with(username, password) {


        println(
            "totalTimeUsage:${
                measureTimeMillis {
                    block(db)
                }
            }"
        )
    }
}