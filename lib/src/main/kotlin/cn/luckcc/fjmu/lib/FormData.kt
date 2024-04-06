package cn.luckcc.fjmu.lib

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import zx.dkk.utils.encodeURIComponent

class FormData(vararg initial:Pair<String,String>):MutableMap<String,String> by mutableMapOf(*initial){
    inline val requestBodyStr:String
        get() {
            val sb = StringBuilder()
            forEach { (key, value) ->
                sb.append("${encodeURIComponent(key)}=${encodeURIComponent(value)}&")
            }
            sb.deleteAt(sb.lastIndex)
            return sb.toString()
        }
}
@Suppress("NOTHING_TO_INLINE")
inline fun HttpRequestBuilder.setFormBody(formData: FormData){
    contentType(ContentType.Application.FormUrlEncoded)
    setBody(formData.requestBodyStr)
}