package cn.luckcc.fjmu.lib

import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.util.date.*
import java.util.concurrent.atomic.AtomicLong

private class CookieStore{
    private val oldest=AtomicLong(Long.MAX_VALUE)
    private val cookies= hashMapOf<String,Cookie>()
    fun addCookie(cookie: Cookie){
        val name=cookie.name
        val expiredMilli=cookie.expires?.timestamp?: (GMTDate().timestamp+24*60*60*1000L)
        oldest.set(minOf(oldest.get(),expiredMilli))
        cookies[name]=cookie
    }
    private val lock=Any()
    private fun removeExpired(timestamp:Long)= synchronized(lock){
        cookies.forEach { (name, pair) ->
            if (pair.expires!=null&&pair.expires!!.timestamp<timestamp)
                cookies.remove(name)
        }
        oldest.set(cookies.minOf { it.value.expires?.timestamp?:Long.MAX_VALUE })
    }
    fun getCookies():List<Cookie>{
        val timestamp= GMTDate().timestamp
        if (oldest.get()>timestamp)
            removeExpired(timestamp)
        return cookies.values.toList()
    }
    operator fun get(key:String):String?{
        return cookies[key]?.value
    }
}

internal class ScheduleCookieStorage :CookiesStorage {
    private val cookies= hashMapOf<String, CookieStore>()
    private val lock=Any()
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = synchronized(lock){
        println("host:${requestUrl.host}")
        println("append cookie:$cookie")
        if (cookie.name.isBlank())
            return@synchronized
        val cookiesInDomain=cookies[requestUrl.host]?: CookieStore()
        cookiesInDomain.addCookie(cookie)
        cookies[requestUrl.host]=cookiesInDomain
    }

    fun clearCookies(){
        cookies.clear()
    }

    override fun close() {

    }
    fun getCookieFor(targetHost: String, name: String)=getCookiesForHost(targetHost).find {
        it.name==name
    }?.value
    fun getCookiesForHost(targetHost:String):List<Cookie>{
        val cookies= arrayListOf<Cookie>()
        for ((host,store)in this.cookies){
            if (targetHost.endsWith(host)||host.endsWith(targetHost)){
                cookies.addAll(store.getCookies())
            }
        }
        return cookies
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val cookies=getCookiesForHost(requestUrl.host)
        println("********************************************")
        println("get cookies for host:${requestUrl.host}")
        for (cookie in cookies){
            println(cookie)
        }
        println("********************************************")
        return cookies
    }
}
