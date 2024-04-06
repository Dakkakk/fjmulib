package cn.luckcc.fjmu.lib

import zx.dkk.utils.encodeURIComponent

class QueryParametersBuilder {
    private val map = linkedMapOf<String, String>()
    fun <T> append(name: String, value: T?) {
        if (value == null)
            return
        map[name] = value.toString()
    }

    fun append(name: String, value: String?) {
        if (value == null)
            return
        map[name] = value
    }

    fun build(): String {
        return map.qPToString()
    }

    private fun Map<String, String?>.qPToString(): String {
        val qP = StringBuilder()
        for ((name, value) in this) {
            if (value == null)
                continue
            qP.append("${encodeURIComponent(name)}=${encodeURIComponent(value)}&")
        }
        val param = qP.toString()
        return if (param == "")
            ""
        else
            "?${qP.toString().removeSuffix("&")}"
    }
}
internal fun buildQueryParameters(block: QueryParametersBuilder.() -> Unit): String {
    return QueryParametersBuilder().run {
        block()
        build()
    }
}