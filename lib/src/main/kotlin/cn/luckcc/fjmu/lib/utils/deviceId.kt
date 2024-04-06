package cn.luckcc.fjmu.lib.utils

import zx.dkk.utils.callJs

val deviceIdCode="""
    function deviceId() {
        try {
            return "xxxyxxx-xxxx-xxx".replace(
                /[xy]/g,
                function(e) {
                    var t = 16 * Math.random() | 0;
                    return ("x" == e ? t : 3 & t | 8).toString(16)
                }
            )
        } catch (e) {
            return ""
        }
    }
""".trimIndent()
//private fun deviceId() = callJs(deviceIdCode, "deviceId", "deviceId") as String

fun deviceId():String{
    return "xxxyxxx-xxxx-xxx".map {
        if (it=='-')'-'
        else generator(it.toString())
    }.joinToString()
}
private fun generator(e:String):String{
    val t=16*Math.random().toInt().or(0)
    return (if ("x"==e) t else t.or(8)).toString(16)
}