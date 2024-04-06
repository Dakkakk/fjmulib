@file:Suppress("NonAsciiCharacters", "LocalVariableName", "UNUSED_VARIABLE")

package cn.luckcc.fjmu.lib

import io.ktor.client.*
import io.ktor.client.content.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import cn.luckcc.fjmu.lib.def.Default
import cn.luckcc.fjmu.lib.def.comeYear
import cn.luckcc.fjmu.lib.msg.*
import cn.luckcc.fjmu.lib.proto.*
import cn.luckcc.fjmu.lib.proto.course.*
import cn.luckcc.fjmu.lib.proto.paper.SimplePaper
import cn.luckcc.fjmu.lib.response.*
import cn.luckcc.fjmu.lib.utils.deviceId
import cn.luckcc.fjmu.lib.utils.encryptAES
import cn.luckcc.fjmu.lib.utils.encryptBase64
import cn.luckcc.fjmu.lib.utils.verification
import org.slf4j.LoggerFactory
import zx.dkk.utils.collections.NONE
import zx.dkk.utils.collections.autoCopyWhenEqual
import zx.dkk.utils.collections.singletonListOf
import zx.dkk.utils.collections.toSingletonList
import zx.dkk.utils.messagechain.MessageSender
import zx.dkk.utils.time.*
import java.io.Closeable
import java.net.InetSocketAddress
import java.net.ProxySelector
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set
import kotlin.math.min
import kotlin.random.Random

var useVpnProxy: Boolean = false

class ScheduleClient(
    var username: String,
    var password: String,
    var rememberUser: Boolean,
) : Closeable {
    private val logger = LoggerFactory.getLogger("ScheduleClient")
    private fun debug(message: String) = logger.debug(message)
    private fun info(message: String) = logger.info(message)
    private fun error(message: String) = logger.error(message)
    val cookieStorage = AcceptAllCookiesStorage()
    fun applyToDefault() {
        Default.apply(username, password)
    }


    @Suppress("SpellCheckingInspection")
    private val client = HttpClient(OkHttp) {
        install(HttpCookies) {
            storage = cookieStorage
        }
        defaultRequest {
            headers {
                append(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
                )
//                append(HttpHeaders.Origin, url.clone().buildString())
//                append("Upgrade-Insecure-Requests", "1")
            }
        }
        engine {
            config {
                followSslRedirects(false)
                followRedirects(false)
                retryOnConnectionFailure(true)
                dispatcher(Dispatcher())
                callTimeout(15, TimeUnit.SECONDS)
                if (useVpnProxy)
                    proxySelector(ProxySelector.of(InetSocketAddress("127.0.0.1", 8888)))
            }
        }
    }
    lateinit var imgCodeGetter: suspend () -> String

    //    private suspend inline fun <reified T> HttpResponse.body(): T {
//        return decode(readBytes())
//    }

    fun checkEoneStatus(body: String?): Boolean {
        if (body == null) {
            debug("Unknown Error,cannot get eone status, please check internet")
            return false
        } else if (body.contains("智慧福医")) {
            debug("EONE portal passed")
            return true
        } else {
            debug("EONE portal denied, please sign in")
            return false
        }
    }

    suspend fun checkEoneStatus() = checkEoneStatus(client.get("https://eone.fjmu.edu.cn/web/").bodyAsText())

    suspend fun loginEONE(): Boolean {
        val postBody = """
              {
                  "user":"${username}",
                  "password":"${encryptBase64(password)}",
                  "mode":"UsernameAndPasswordAndVerifyCode",
                  "ext":{
                      "imageCode":"${imgCodeGetter()}",
                      "type":1,
                      "code":"",
                      "isBintang":0
                  },
                  "lang":"zh_CN"
              }
        """.trimIndent()
        val oauthResponse = post(
            url = "https://eone.fjmu.edu.cn/oauth2/login",
        ) {
            contentType(ContentType.Application.Json)
            setBody(postBody)
            cookie("contextPath", "")
            header("Origin", "https://eone.fjmu.edu.cn")
            header("Referer", "https://eone.fjmu.edu.cn/")
            header("Sec-Fetch-Dest", "empty")
            header("Sec-Fetch-Mode", "no-cors")
            header("Sec-Fetch-Site", "same-origin")
        }
        if (oauthResponse == null) {
            debug("Got null when trying to login to EONE portal")
            debug("Please check the internet or contact dev for help")
            return false
        }
        val body = oauthResponse.bodyAsText()
        if (body.contains("redirect")) {
            debug("EONE portal passed, continue back logics")
            return true
        } else {
            debug("EONE portal denied, please check img code or username/password.->$body")
            return false
        }
    }

    suspend fun get(
        url: String,
        block: ((QueryParametersBuilder.() -> Unit)?) = null,
    ): HttpResponse? {
        debug("GET:$url")
        val response = try {
            client.get("$url${if (block == null) "" else buildQueryParameters(block)}")
        } catch (e: Exception) {
            null
        }?.let { response ->
            if (response.call.request.url.toString()
                    .contains("http(s)?://authserver(\\.webvpn)?\\.fjmu\\.edu\\.cn/authserver/login".toRegex())
            ) {
                info("login required")
                val body = response.bodyAsText()
                val checkResponse = loginFromRequest(url, username, password, body, null) ?: return@let null
                val pwdState = loginResultToPWDState(checkResponse.bodyAsText(),)
                info("pwdState=$pwdState")
                if (pwdState != PwdCheckState.Correct)
                    null
                else get(url, block)
            } else if (response.call.request.url.toString()
                    .contains("http(s)?://eone(-443\\.webvpn)?\\.fjmu\\.edu\\.cn/oauth2/authorize".toRegex())
            ) {
                info("secondary login check required")

                val status = loginEONE()
                if (status) {
                    get(url, block)
                } else {
                    null
                }
            } else
                response
        }
        val location = response?.headers?.get(HttpHeaders.Location)

        if (location != null) {
            println("location:$location")
            return get(location)
        }
        return response

    }

//    private suspend inline fun <reified Body> post(
//        url: String,
//        body: Body,
//    ) = post(
//        url = url,
//    ) {
//        setBody(encode(body))
//    }

    suspend fun post(
        url: String,
        builder: HttpRequestBuilder.() -> Unit
    ): HttpResponse? {
        println("POST:$url")
        val response = try {
            client.post(url, builder)
        } catch (e: Exception) {
            null
        }
        val location = response?.headers?.get(HttpHeaders.Location)
        if (location != null) {
            return get(location)
        }
        return response
    }


    private fun makeLoginFormFromResponseBody(
        responseBodyStr: String,
        captcha: String?,
        username: String,
        password: String,
        remember: Boolean
    ): FormData {
        val document = Jsoup.parse(responseBodyStr)
        val form = document.select("form")[0]
        val pwdDefaultEncryptSalt = document.getElementById("pwdDefaultEncryptSalt")!!.`val`()
        val data = FormData()
        val inputs = form.select("input")
        for (input in inputs) {
            val name = input.attr("name")
            val value = input.`val`()
            if (name.isEmpty())
                continue
            if (name == "rememberMe")
                continue
            data[name] = value
        }
        if (remember)
            data["rememberMe"] = "on"
        data["username"] = username
        //todo try to use aes in lib
        data["password"] = encryptAES(password, pwdDefaultEncryptSalt)
        if (captcha != null)
            data["captchaResponse"] = captcha
        return data
    }

    @Suppress("unused")
    suspend fun captcha() =
        get("https://authserver.webvpn.fjmu.edu.cn/authserver/captcha.html?ts=${System.currentTimeMillis() % 1000}")?.bodyAsChannel()


    suspend fun imgCode() =
        get("https://eone.fjmu.edu.cn/liveid/captcha/generateCode?type=4&r=${Math.random()}")?.bodyAsChannel()

    @Suppress("unused")
    suspend fun needCaptcha(username: String) =
        get("https://authserver.webvpn.fjmu.edu.cn/authserver/needCaptcha.html?username=${username}&pwdEncrypt2=pwdEncryptSalt&_=${System.currentTimeMillis()}")?.bodyAsText()
            .toBoolean()

    /**
     * 检查是否需要评教
     */
    private suspend fun MessageSender.checkNeedDoPaper(
        tokenInitial: String? = null,
    ): Boolean {
        val token = tokenInitial ?: getToken()
        val response = get("https://jxzhpt.fjmu.edu.cn/api/v2/core/Schedule/today?token=$token")?.bodyAsText() ?: ""
        return response.contains("评教")
    }

    private suspend fun loginFromRequest(
        srcUrl: String,
        username: String,
        password: String,
        authServerHtml: String,
        captcha: String? = null
    ): HttpResponse? {
        if (!authServerHtml.contains("casLoginForm")) {
            return null
        }
        val form = makeLoginFormFromResponseBody(authServerHtml, captcha, username, password, rememberUser)
        return post(
            url = srcUrl
        ) {
            setFormBody(form)
        }
    }

    private fun loginResultToPWDState(body: String) =
        if (!body.contains("auth_error") && body != "")
            PwdCheckState.Correct
        else if (body.contains("用户名或者密码有误"))
            PwdCheckState.UsernamePasswordNotRight
        else if (body.contains("无效的验证码"))
            PwdCheckState.CaptchaNotRight
        else if (body.contains("请输入验证码"))
            PwdCheckState.CaptchaRequire
        else
            PwdCheckState.Error

    /**
     * 记得在下次[checkPasswordAccuracy]之前[logout]
     */
    private suspend fun checkPasswordAccuracy(
        username: String,
        password: String,
        captcha: String?,
        webvpn: Boolean = true
    ): PwdCheckState {
        val url = "https://authserver${if (webvpn) ".webvpn" else ""}.fjmu.edu.cn/authserver/login"
        val body1 = client.get(url).bodyAsText()
        val loginResult=loginResultToPWDState(
            loginFromRequest(
                url,
                username,
                password,
                body1,
                captcha
            )?.bodyAsText() ?: ""
        )
        return loginResult
    }

    /**
     * @see [checkPasswordAccuracy]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun checkPasswordAccuracy(captcha: String?, webvpn: Boolean = true) =
        checkPasswordAccuracy(username, password, captcha, webvpn)

    private suspend fun MessageSender.connectWebVpn(): Boolean {
        postMessage("尝试连接WebVpn", 0f)
        val body = get("https://webvpn.fjmu.edu.cn")?.bodyAsText() ?: ""
        if (body.contains("福建医科大学 WebVPN")) {
            postMessage("连接成功", 0f)
            return true
        }
        postMessage("无法连接到WebVpn", 0f)
        return false
    }

    private suspend fun MessageSender.connectJWGL(): Boolean {
        if (!connectWebVpn()) {
            postMessage("cannot connect webVPN", 100f)
            return false
        }
        postMessage("尝试连接教务管理系统", 0f)
        val tryConnectJW =
            get("https://authserver.webvpn.fjmu.edu.cn/authserver/login?service=https%3A%2F%2Fjwglxt.webvpn.fjmu.edu.cn%2Fsso%2Fjznewsixlogin")?.bodyAsText()
                ?: ""
        if (tryConnectJW.contains("casLoginForm")) {
            postMessage("连接失败，需要登录", 0f)
            return false
        } else if (tryConnectJW.contains("用户登录")) {
            val retry = get("https://jwglxt-443.webvpn.fjmu.edu.cn/sso/jznewsixlogin")?.bodyAsText() ?: ""
            if (retry.contains("用户登录")) {
                postMessage("连接失败，需要登录", 0f)
                return false
            }
            if (retry.contains("errors")) {
                postMessage("错误", 0f)
                return false
            }
        }
        postMessage("成功连接到教务管理系统", 0f)
        return true
    }

    @Suppress("unused")
    suspend fun getCurrentSchoolYearAndSemester(
        needConnectJWGL: Boolean = true
    ) =
        withSender(processGetCurrSchoolYearSemester, processGetCurrSchoolYearSemester) {
            if (needConnectJWGL && !connectJWGL())
                return@withSender fail("无法获取当前学期信息")
            val body =
                get("https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default&su=${username}")?.bodyAsText()
                    ?: ""
            val document = Jsoup.parse(body)
            val options = document.select("option[selected=selected]")
            val schoolYear = options[0].`val`().toInt()
            val semester = Semester.ofVal(options[1].`val`())
            success("成功获取学期信息[${schoolYear}学年第${semester}学期]", SemesterPair(schoolYear, semester))
        }

    suspend fun getAllAvailableSchoolYearsAndCurrentValue(
        needConnectJWGL: Boolean = true
    ) = withSender(processGetCurrSchoolYearSemester, processGetCurrSchoolYearSemester) {
        if (needConnectJWGL && !connectJWGL())
            return@withSender fail("无法获取当前学期信息")
        val body =
            get("https://jwglxt-443.webvpn.fjmu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default&su=${username}")?.bodyAsText()
                ?: ""
        val document = Jsoup.parse(body)
        val options = document.select("option[selected=selected]")
        val schoolYear = options[0].`val`().toInt()
        val semester = Semester.ofVal(options[1].`val`())
        val years = document.select("option[value~=[0-9]{4}]").map { it.`val`().toInt() }
        success("成功获取学期信息[${schoolYear}学年第${semester}学期]", (SemesterPair(schoolYear, semester)) to years)
    }

    private suspend inline fun loadRecommendCourseTotal(schoolYear: Int, semester: Semester): List<TeachingClass> {
        val recommendTableListBody = get(recommendTimetableListUrl(username))?.bodyAsText() ?: ""
        val requestBody = makeTimetableRequestBody(recommendTableListBody, schoolYear, semester)
        val recommendTableBody = post(recommendTimetableUrl(username)) {
            setFormBody(requestBody)
        }?.bodyAsText() ?: ""
        return decodeBody<RecommendTableResponse>(recommendTableBody)?.kbList?.asSequence()
            ?.filterNot { kb ->
                kb.teachingClassName == NONE || kb.teachingClassName == "" || kb.courseName.contains("板块") || kb.courseId.contains(
                    "板块"
                )
            }?.map {
                it.asTeachingClass(schoolYear, semester)
            }?.toList() ?: emptyList()
    }

    suspend fun getTeachingClasses(
        semesterPair: ISemesterPair,
        needConnectJWGL: Boolean = true,
        useRecommendTable: Boolean = false,
    ): List<TeachingClass>? =
        withSender("$processGetCourseData[${semesterPair.value}]教学班Acquire", processGetCourseData) {
            val (schoolYear, semester) = semesterPair
            withContext(Dispatchers.IO) {
                val teachingClasses = arrayListOf<TeachingClass>()
                postMessage("开始查询教学班", 0f)
                if (needConnectJWGL && !connectJWGL()) {
                    return@withContext fail("无法链接到WebVpn")
                }
                postMessage("链接成功，开始获取可用教学班", .1f)
                val courseTotalRespondBody = post(courseTotalUrl()) {
                    setFormBody(makeCourseTotalRequestBody(schoolYear, semester))
                }?.bodyAsText() ?: ""
                val courseTotals = decodeBody<CourseTotalResponse>(courseTotalRespondBody)
                if (courseTotals == null) {
                    fail("无法获取教学班")
                } else {
                    teachingClasses.addAll(courseTotals.items.map { it.asTeachingClass(schoolYear, semester) })
                    if (useRecommendTable) {
                        teachingClasses.addAll(loadRecommendCourseTotal(schoolYear, semester))
                    }
                    success("成功获取所有可用教学班", teachingClasses.distinct().sorted())
                }
            }
        }

    suspend fun getPapers(
        tokenInitial: String? = null
    ) = withSender(
        name = processGetAllUnFinishedPapers,
        type = processGetAllUnFinishedPapers,
    ) {
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val allPapersResponse = get(
            url = getAllUnFinishedPapersUrl(token)
        )?.bodyAsText() ?: return@withSender fail("无法获取评教信息")
        val paperObjects = jsonParser.decodeFromString<GetAllPapersResponse>(allPapersResponse).data
        if (paperObjects.isEmpty())
            return@withSender success("获取成功，无任何评教需要完成", emptyList())
        val results = arrayListOf<SimplePaper>()
        for (index in paperObjects.indices) {
            val paperObj = paperObjects[index]
            val paramResponse = get(getPaperParamResponseUrl(token, paperObj))?.bodyAsText()
            if (paramResponse == null) {
                postMessage(
                    "无法获取${paperObj.courseName}-${paperObj.teacherName}的评教参数",
                    index.toFloat() / paperObjects.size
                )
                continue
            }
            val paperParam = jsonParser.decodeFromString<GetPaperParamResponse>(paramResponse).paperParam
            val paperContentResponse = get(getPaperContentResponseUrl(paperObj, token))?.bodyAsText()
            if (paperContentResponse == null) {
                postMessage(
                    "无法获取${paperObj.courseName}-${paperObj.teacherName}的评教内容",
                    index.toFloat() / paperObjects.size
                )
                continue
            }
            val paperContent = jsonParser.decodeFromString<PaperContentResponse>(paperContentResponse).paperContent
            val simplePaper = SimplePaper(paperObj, paperParam, paperContent)
            postMessage(
                "成功获取${simplePaper.courseName}-${simplePaper.teacherName}的评教",
                index.toFloat() / paperObjects.size
            )
            results.add(simplePaper)
        }
        if (results.isEmpty()) {
            return@withSender fail("获取评教失败")
        }
        success("成功获取${results.size}个评教信息", results)
    }

    private val scores = doubleArrayOf(
        0.95, .965, .97, .98, .995,//19
        0.9, 0.915, 0.92, .93, .945,//18
    )

    private suspend fun MessageSender.paperWork(
        simplePaper: SimplePaper,
        deviceId: String? = null,
        autoFill: Boolean = false,
        tokenInitial: String? = null,
        isSave: Boolean
    ): String? {
        val token = tokenInitial ?: getToken() ?: return fail("无法获取token")
        if (autoFill) {
            val rand = Random(now.epochMilli)
            val ansScores = scores.sortedBy {
                rand.nextInt()
            }
            for (index in simplePaper.questions.indices) {
                val question = simplePaper.questions[index]
                question.answerScore = ansScores[index] * question.totalScore
            }
        }
        if (simplePaper.getFirstUnFinishedQuestion() != null) {
            return fail("评教未完成")
        }

        val postBody =
            simplePaper.toResultJson(verification(simplePaper.verificationSrc), deviceId ?: deviceId(), isSave)
        val response = post(
            url = "https://jxzhpt.webvpn.fjmu.edu.cn/api/v2/questionnaire/paper/${simplePaper.paperId}?token=$token"
        ) {
            setBody(postBody)
            contentType(ContentType.Application.Json)
        }?.bodyAsText() ?: return fail("无响应，评教可能失败")
        return response
    }

    suspend fun getPaperWorkStatus(
        tokenInitial: String? = null
    ) = withSender(
        name = "$processDoPaper-status",
        type = "$processDoPaper-status"
    ) {
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val response = get(getPaperWorkStatusUrl(token))?.bodyAsText() ?: return@withSender fail("无法获取评教完成状态")
        val status = jsonParser.decodeFromString<PaperWorkStatusResponse>(response).paperWorkStatus
        success("成功获取评教状态", status)
    }

    suspend fun doPaper(
        simplePaper: SimplePaper,
        deviceId: String? = null,
        autoFill: Boolean = false,
        tokenInitial: String? = null
    ) = withSender(
        name = "$processDoPaper ${simplePaper.courseName}-${simplePaper.teacherName}",
        type = processDoPaper
    ) {
        val response = paperWork(simplePaper, deviceId, autoFill, tokenInitial, false)
            ?: return@withSender fail("无法评教")
        val body = jsonParser.decodeFromString<DoPaperResponse>(response)
        val score = body.id
        if (score != null)
            success("获取成功", score)
        else
            fail(body.message)
    }

    suspend fun savePaper(
        simplePaper: SimplePaper,
        deviceId: String? = null,
        autoFill: Boolean = false,
        tokenInitial: String? = null
    ) = withSender(
        name = "$processDoPaper-save ${simplePaper.courseName}-${simplePaper.teacherName}",
        type = "$processDoPaper-保存评教"
    ) {
        val response = paperWork(simplePaper, deviceId, autoFill, tokenInitial, true)
            ?: return@withSender fail("无法评教")
        val body = jsonParser.decodeFromString<SavePaperResponse>(response)
        val score = body.score
        if (score != null)
            success("获取成功", score)
        else
            fail(body.message)
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    suspend fun getCoursesForTeachingClasses(
        semesterPair: ISemesterPair,
        teachingClasses: List<TeachingClass>,
        needConnectJWGL: Boolean = true,
        colorSlotId: (index: Int, courseId: String) -> Int
    ) = withSender("$processGetCourseData[${semesterPair.value}]", processGetCourseData) {
        val (schoolYear, semester) = semesterPair
        withContext(Dispatchers.IO) {
            if (teachingClasses.isEmpty()) {
                return@withContext success("该学期课程数为0", SemesterCourse.EmptyData)
            }
            val courses = singletonListOf<Course>(autoCopyWhenEqual())
            val faculties = singletonListOf<Faculty>(autoCopyWhenEqual())
            val klasses = singletonListOf<Clazz>(clazzComparator, autoCopyWhenEqual())
            val teachers = singletonListOf<Teacher>(autoCopyWhenEqual())
            val classrooms = singletonListOf<Classroom>(autoCopyWhenEqual())
            var firstDayHavingClass = Long.MAX_VALUE
            postMessage("开始查询课表", 0f)
            if (needConnectJWGL && !connectJWGL()) {
                return@withContext fail("无法链接到WebVpn")
            }
            postMessage("链接成功，开始获取课表", .1f)
            val progressEachStep = (.9f / teachingClasses.size) * 0.5f
            val step = AtomicInteger(1)
            val responses = hashMapOf<String, Pair<Deferred<List<CourseExItem>?>, Deferred<List<CourseDaily>?>>>()
            for (teachingClassIndex in teachingClasses.indices) {
                val teachingClass = teachingClasses[teachingClassIndex]
                val 教学班名称 = teachingClass.教学班名称
                val deferred1 = async {
                    val exBody = post(courseExUrl(username)) {
                        setFormBody(courseExRequestBody(schoolYear, semester, teachingClass.教学班名称))
                    }?.bodyAsText() ?: ""
                    val items = decodeBody<CourseTotalExResponse>(exBody)?.items
                    if (items == null) {
                        postMessage(
                            "无法加载${teachingClass.教学班名称}的信息",
                            progressEachStep * (step.getAndIncrement()) + .1f
                        )
                        null
                    } else {
                        postMessage(
                            "已加载${teachingClass.教学班名称}的信息",
                            progressEachStep * (step.getAndIncrement()) + .1f
                        )
                        items
                            .filter {
                                it.教学班名称 == 教学班名称
                            }
                    }
                }
                val deferred2 = async {
                    val dailyBody = post(courseDailyUrl(username)) {
                        setFormBody(makeCourseDailyRequestBody(schoolYear, semester, teachingClass.教学班名称))
                    }?.bodyAsText() ?: ""
                    val items = decodeBody<CourseDailyResponse>(dailyBody)?.items
                    if (items == null) {
                        postMessage(
                            "无法加载${teachingClass.教学班名称}的信息",
                            progressEachStep * (step.getAndIncrement()) + .1f
                        )
                        null
                    } else {
                        postMessage(
                            "已加载${teachingClass.教学班名称}的信息",
                            progressEachStep * (step.getAndIncrement()) + .1f
                        )
                        items
                            .filter {
                                it.教学班名称 == 教学班名称
                            }
                    }
                }
                responses[教学班名称] = (deferred1 to deferred2)
            }
            for ((教学班名称, pair) in responses) {
                val (def1, def2) = pair
                val items = def1.await() ?: continue
                val dailies = def2.await() ?: continue
                for (item in items) {
                    courses.add(item.asCourse(schoolYear, semester))
                    faculties.add(item.asFaculty())
                }
                for (daily in dailies) {
                    val clazz = daily.asKlass(schoolYear, semester)
                    firstDayHavingClass = min(clazz.epochDay, firstDayHavingClass)
                    klasses.add(clazz)
                    teachers.add(daily.asTeacher())
                    classrooms.add(daily.asClassroom())
                }
            }
            val sortedCourses = courses.sorted()
            sortedCourses.forEachIndexed { index, course ->
                course.index = colorSlotId(index, course.课程ID)
            }
            require(firstDayHavingClass < Long.MAX_VALUE) {
                "Unable to set firstDayHavingClass"
            }
            val semesterCourse = SemesterCourse(
                semesterPair.toYearSemester(firstDayHavingClass.epochDay.with(DayOfWeek.MONDAY).epochDay),
                classrooms.toList(),
                teachers.toList(),
                sortedCourses,
                teachingClasses,
                faculties.toList(),
                klasses.toList(),
            )
            semesterCourse.mappingData()
            courses.forEach { course ->
                course.学时分类 = course.clazzes.groupBy {
                    it.学时类型
                }.map { (type, values) ->
                    type to values.sumOf { it.学时 }
                }
            }
            success("课程信息加载完成", semesterCourse)
        }
    }

    /**
     * @param dayCount 自[nowDate]起，往前加载多少天的内容
     */
    suspend fun getBulletins(
        dayCount: Long = 5
    ) = withSender(processGetBulletins, processGetBulletins) {
        withContext(Dispatchers.IO) {
            val max = nowDate
            val min = max.minusDays(dayCount)
            var requestBody: FormData? = null
            val links = mutableListOf<Link>()
            postMessage("开始获取链接", 0f)
            var page = 1
            while (page < 10) {
                val pageBody = if (requestBody == null) {
                    get("https://192-168-188-100-81.webvpn.fjmu.edu.cn/archives/docc/new.aspx?TypeID=2")
                } else {
                    post("https://192-168-188-100-81.webvpn.fjmu.edu.cn/archives/docc/new.aspx?TypeID=2") {
                        setFormBody(requestBody!!)
                    }
                }?.bodyAsText() ?: ""
                val document = Jsoup.parse(pageBody)
                val linksInPage = document.allLinks()
                links.addAll(document.allLinks())
                postMessage("读取第${page}页的内容", (page.coerceIn(0, 10).toFloat() / 10) * 0.1f)
                if (linksInPage.any { it.date <= min }) {
                    break
                }
                requestBody = document.nextPageRequestBody()
                page++
            }
            val facultyPattern = "来源于：([^ ]+) .+".toPattern()
            val progress = AtomicInteger(1)

            @Suppress("SpellCheckingInspection")
            val defers = links.map { link ->
                async {
                    val bulletinBody = get(link.link)?.bodyAsText() ?: ""
                    val doc = Jsoup.parse(bulletinBody)
                    val title = doc.selectFirst("ul[class=con_title]")?.text() ?: return@async null
                    val paragraphs = doc.selectFirst("div[class=wenben]")?.select("p") ?: return@async null
                    val sb = StringBuilder()
                    for (paragraph in paragraphs) {
                        sb.append(paragraph.text())
                        sb.append('\n')
                    }
                    val info = doc.selectFirst("div[class=xinxi]")?.text() ?: return@async null
                    val facultyMatcher = facultyPattern.matcher(info)
                    if (!facultyMatcher.matches())
                        return@async null
                    val faculty = facultyMatcher.group(1) ?: return@async null
                    val content = sb.toString()
                    val attachments = doc.attachments()
                    val bulletin = Bulletin(
                        link.newsId,
                        title, faculty, link.date.epochDay, content,
                        链接 = link.link
                    )
                    bulletin.addAll(attachments)
//                    bulletins.add(bulletin)
                    postMessage("解析得到${title}", .1f + ((progress.getAndIncrement()).toFloat() / links.size) * 0.9f)
                    bulletin
                }
            }
            success("加载完成", defers.mapNotNull { it.await() })
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun Document.attachments(): List<Attachment> {
        val pattern = "(.+)<a href=\"(.+)\".+".toPattern()
        val spaceReg = " +".toRegex()
        val contents = selectFirst("div[class=dayin]")?.selectFirst("td")?.html()?.split("<br>")
        if (contents.isNullOrEmpty())
            return emptyList()
        val attachments = arrayListOf<Attachment>()
        for (content in contents) {
            if (!content.contains("<a href="))
                continue
            val matcher = pattern.matcher(content)
            if (!matcher.matches())
                continue
            val text = matcher.group(1)?.replace(Regex("(&nbsp;)|\n"), " ")?.replace(spaceReg, " ")
            val g2 = matcher.group(2)
            if (text == null || g2 == null)
                continue
            val link = "https://192-168-188-100-81.webvpn.fjmu.edu.cn/archives/docc/$g2".toHttpUrl().toString()
            attachments.add(Attachment(text, link))
        }
        return attachments
    }

    private data class Link(
        var link: String = "",
        var faculty: String = "",
        var label: String = "",
        var newsId: Int = 0,
        var date: LocalDate
    )

    private fun Document.allLinks(): List<Link> {
        val links = arrayListOf<Link>()
        val elements = body().getElementsByAttributeValueMatching("id", "^DataGrid1_ctl[0-9]+_Label4$")
            .map { it.parent()?.parent()?.parent() }
        for (element in elements) {
            if (element == null)
                continue
            val values = element.text().split(" ")
            if (values.size != 3)
                continue
            val attr = element.getElementsByTag("a").attr("href").split("=")
            if (attr.size != 2)
                continue
            val newsId = attr[1].toInt()
            links.add(
                Link(
                    newsId = newsId,
                    label = values[0],
                    faculty = values[1],
                    date = parseDate(values[2]) ?: nowDate,
                    link = "https://192-168-188-100-81.webvpn.fjmu.edu.cn/archives/docc/newshow.aspx?NewsID=$newsId"
                )
            )
        }
        return links
    }

    private fun parseDate(date: String): LocalDate? {
        val values = date.split("/")
        if (values.size != 3)
            return null
        return LocalDate.of(values[0].toInt(), values[1].toInt(), values[2].toInt())
    }

    @Suppress("SpellCheckingInspection")
    private fun Document.nextPageRequestBody(): FormData {
        val form = FormData()
        val inputs = getElementsByTag("input")
        form["__EVENTTARGET"] = "LinkButton2"
        form["__EVENTARGUMENT"] = ""
        for (input in inputs) {
            when (input.id()) {
                "__VIEWSTATE" -> {
                    form["__VIEWSTATE"] = input.`val`()
                }

                "__VIEWSTATEGENERATOR" -> {
                    form["__VIEWSTATEGENERATOR"] = input.`val`()
                }

                "__EVENTVALIDATION" -> {
                    form["__EVENTVALIDATION"] = input.`val`()
                }
            }
        }
        form["TextBox4"] = ""
        return form
    }

    suspend fun getBulletinAttachment(
        attachment: Attachment,
        downloadListener: ProgressListener? = null,
        block: suspend ByteReadChannel.(suggestedName: String, suggestedExtension: String) -> Unit
    ) {
        val response = client.get(attachment.附件下载链接) {
            onDownload(downloadListener)
        }
        response.bodyAsChannel().block(attachment.附件名称, attachment.附件下载链接.substringAfterLast('.', "bin"))
    }

    suspend fun getCreditEachCourse(
        needPwdCheck: Boolean = true,
        checkDoPaperInitial: Boolean = true,
        tokenInitial: String? = null
    ) = withSender(processGetCredit, processGetCredit) {
        postMessage("开始获取绩点详情", 0f)
        if (checkDoPaperInitial && checkNeedDoPaper(tokenInitial)) {
            return@withSender fail("无法加载绩点详情，因为需要评教")
        }
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val response = get(creditDetailUrl(token))?.bodyAsText() ?: ""
        val data = decodeBody<CreditResponse>(response)?.data
        if (data == null)
            fail("获取失败")
        else
            success("绩点详情获取完成", data.toSingletonList(autoCopyWhenEqual()).toList())
    }

    suspend fun getCreditOverview(
        checkDoPaperInitial: Boolean = true,
        tokenInitial: String? = null
    ) = withSender(processGetCreditOverview, processGetCreditOverview) {
        postMessage("开始获取绩点总览", 0f)
        if (checkDoPaperInitial && checkNeedDoPaper(tokenInitial)) {
            return@withSender fail("无法加载绩点详情，因为需要评教")
        }
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val response = get(creditOverviewUrl(token))?.bodyAsText() ?: ""
        val data = decodeBody<CreditOverviewResponse>(response)?.data?.filter {
            !it.绩点分类名称.contains("平均绩点")
        }
        if (data == null) {
            fail("获取失败")
        } else
            success("绩点总览获取完成", data)
    }

    suspend fun getRank(
        needPwdCheck: Boolean = true,
        checkDoPaperInitial: Boolean = true,
        tokenInitial: String? = null
    ) = withSender(processGetRanks, processGetRanks) {
        postMessage("开始获取排名", 0f)
        if (checkDoPaperInitial && checkNeedDoPaper(tokenInitial)) {
            return@withSender fail("无法加载绩点详情，因为需要评教")
        }
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val respond = get(getRankUrl(token))?.bodyAsText() ?: ""
        val ranks = decodeBody<RankResponse>(respond)?.data
        if (ranks == null) {
            fail("获取失败")
        } else
            success("排名获取完成", ranks)
    }

    suspend fun getCETResults(
        needPwdCheck: Boolean = true,
        checkDoPaperInitial: Boolean = true,
        tokenInitial: String? = null
    ) = withSender(processGetCETResult, processGetCETResult) {
        postMessage("开始获取CET成绩", 0f)
        if (checkDoPaperInitial && checkNeedDoPaper(tokenInitial)) {
            return@withSender fail("无法加载绩点详情，因为需要评教")
        }
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val response = get(cetResultUrl(token))?.bodyAsText() ?: ""
        val data = decodeBody<CETResultResponse>(response)?.data
        if (data == null) {
            fail("获取失败")
        } else
            success("CET成绩获取成功", data)
    }

    @Suppress("unused")
    suspend fun getStudentInfo(
        tokenInitial: String? = null,
    ) = withSender(processGetSimpleInfo, processGetSimpleInfo) {
        postMessage("开始获取信息", 0f)
        val token = tokenInitial ?: getToken() ?: return@withSender fail("无法获取token")
        val response = get(simpleStudentInfoUrl(token))?.bodyAsText() ?: ""
        val data = decodeBody<StudentInfoSimpleResponse>(response)?.data?.firstOrNull()
        if (data == null) {
            fail("获取失败")
        } else {
            comeYear = data.入学年份
            println("comeYear=$comeYear")
            Default.config.write()
            success("获取成功", data)
        }
    }

    private suspend fun <T> withSender(name: String, type: String, block: suspend MessageSender.() -> T): T? {
        return zx.dkk.utils.messagechain.withSender(name, type) {
            try {
                block()
            } catch (e: Exception) {
                println("error when $type/$name")
                e.printStack()
                fail("${e.message}")
            }
        }
    }

    @Suppress("unused")
    suspend fun getBookRequest() = withSender(processGetBookRequest, processGetBookRequest) {
        postMessage("开始查询教材征订", 0f)
        val response = get("https://111-186-254-50.webvpn.fjmu.edu.cn/jc_xsxy_xy.aspx")?.bodyAsText() ?: ""
        val document = Jsoup.parse(response)
        val trs = document.select("tr:contains(确认选定)")
        val data = arrayListOf<BookRequest>()
        var schoolYearStr = "该"
        var semesterStr = "该"
        for (tr in trs) {
            val tds = tr.select("td").map { it.text() }

            val teacher = tds[0]
            val courseNameWithID = tds[1]
            val bookNameWithISBN = tds[2]
            val schoolYearSemester = tds[3]
            val schoolYear = schoolYearSemester.substring(0, 4).toInt()
            val semester = Semester.ofActualIndex(schoolYearSemester.last() - '0')
            semesterStr = semester.name
            schoolYearStr = schoolYear.toString()
            data.add(
                BookRequest(
                    teacher, courseNameWithID, bookNameWithISBN, schoolYear, semester
                )
            )
        }
        success("${schoolYearStr}学年第${semesterStr}学期共需${data.size}本书", data)
    }

    suspend fun getMessageTypes() = withSender(
        "$processGetMessages-typeGetter",
        processGetMessages
    ) {
        postMessage("开始获取消息类型", 0f)
        val response = get(messageTypeUrl())?.bodyAsText() ?: ""
        val data = decodeBody<MessageTypeResponse>(response)?.data?.messageTypeList
        if (data == null) {
            fail("获取失败")
        } else
            success("消息类型获取成功", data)
    }

    suspend fun getMessages(
        messageType: MessageType,
        size: Int = 20
    ) = withSender(
        "$processGetMessages[${messageType.type}]",
        processGetMessages
    ) {
        postMessage("开始获取[${messageType.type}]的消息", 0f)
        val response = get(messageResponse(messageType, size))?.bodyAsText() ?: ""
        val data = decodeBody<MessageResponse>(response)?.data?.children
        if (data == null) {
            fail("获取失败")
        } else {
            val formatter = "yyyy-MM-dd HH:mm:ss".toDateTimeFormatter()
            postMessage("[${messageType.type}]消息获取成功", 1f)
            success(
                "[${messageType.type}]消息获取成功",
                data.map {
                    Message(
                        内容 = it.content,
                        消息ID = it.id,
                        已读 = it.isReadied,
                        epochMilli = (it.time?.toLocalDateTime(formatter) ?: now).epochMilli,
                        标题 = it.title,
                        消息类型 = it.type,
                        消息类型ID = it.typeId
                    )
                }.sorted()
            )
        }
    }

    @Suppress("unused")
    suspend fun getFreeClassroom(
        campus: Campus,
        weekOfSemester: Int,
        dayOfWeek: DayOfWeek,
        节次: Int
    ) = withSender(
        "$processGetFreeClassrooms[$campus-$weekOfSemester-$dayOfWeek-$节次]",
        processGetFreeClassrooms
    ) {
        getToken()
        postMessage("开始获取空教室", 0f)
        val response = get(freeClassroomUrl(campus, weekOfSemester, dayOfWeek, 节次))?.bodyAsText() ?: ""
        val data = decodeBody<FreeClassroomResponse>(response)?.data
        if (data == null) {
            fail("获取失败")
        } else {
            success("空教室获取成功", data)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun logout() {
        get("https://authserver.webvpn.fjmu.edu.cn/authserver/logout")
    }

    @Suppress("SpellCheckingInspection")
    private suspend fun MessageSender.getToken(): String? {
        debug("Start to GetToken for jxzhpt(.webvpn).fjmu.edu.cn")
        val tokenInitial = cookieStorage.getCookieFor(Url("https://jxzhpt.webvpn.fjmu.edu.cn"), "token")
        if (tokenInitial != null) {
            debug("Successfully Get token from CookieStorage[$tokenInitial]")
            return tokenInitial
        }
        debug("Unable to get token from CookieStorage")
        debug("Try to obtain token over reconnection to webvpn")
        if (!connectWebVpn()) {
            debug("Fail to obtain token over reconnection to webvpn")
            return null
        }
        debug("Successfully connect to webvpn")
//        debug("Start to connect to the portal website 'EONE'")
//        val eoneStatus=loginEONE()
//        if (!eoneStatus){
//            debug("fail to auth EONE portal")
//            return null
//        }
        debug("start oauth2 from eone to jxzhpt")
        val body =
            get("https://eone.fjmu.edu.cn/oauth2/authorize") {
                append("redirect_uri", "https://jxzhpt.webvpn.fjmu.edu.cn/login/return?type=mobile")
                append("response_type", "code")
                append("client_id", "default")
                append("casLogin", "1")
            }?.bodyAsText() ?: ""
        if (body.contains("请输入账号")) {
            debug("unable to oauth from eone to jxzhpt")
            return null
        }
        debug("oauth successfully")
        return cookieStorage.getCookieFor(Url("https://jxzhpt.webvpn.fjmu.edu.cn"), "token")
    }

    suspend fun with(username: String, password: String, block: suspend ScheduleClient.() -> Unit) {
        Default.apply(username, password)
        withDefault(block)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun withDefault(block: suspend ScheduleClient.() -> Unit) {
        this.block()
    }

    companion object {
        private val jsonParser = Json {
            ignoreUnknownKeys = true
        }

        private inline fun <reified T> decodeBody(value: String): T? {
            if (value == "")
                return null
            return jsonParser.decodeFromString(value)
        }

        private const val rememberUserKey = "zx.dkk.schedule.rememberUser"
    }

    override fun close() = client.close()
}

suspend fun AcceptAllCookiesStorage.getCookieFor(url: Url, name: String) = get(url).firstOrNull {
    it.name == name
}?.value