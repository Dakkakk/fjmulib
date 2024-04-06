package cn.luckcc.fjmu.lib.def

import zx.dkk.config.Config
import zx.dkk.utils.crypto.AESCrypto
inline val username:String
    get() = Default.username
inline val password:String
    get() = Default.password
inline var comeYear:Int
    get() = Default.comeYear
    set(value) {
        Default.comeYear =value
    }

object Default{
    private const val usernameKey = "zx.dkk.schedule.username"
    private const val passwordKey = "zx.dkk.schedule.password"
    internal lateinit var config: Config
    private val aes = AESCrypto("#aDL-jK!C,")
    private const val comeYearKey="zx.dkk.schedule.comeYear"
    fun initScheduleField(conf: Config){
        config =conf
    }
    var username: String
        get() = config.getStringValue(usernameKey, "")
        private set(value) {
            config[usernameKey] = value
        }
    var password: String
        get() {
            val `val` = config.getStringValue(passwordKey)
            return if (`val` == null)
                ""
            else
                aes.decrypt(`val`)
        }
        set(value) {
            config[passwordKey] = aes.encrypt(value)
        }
    fun apply(username:String,password:String){
        Default.username =username
        Default.password =password
        config.write()
    }
    var comeYear:Int
        get() {
            return config.getIntValue(comeYearKey,0)
        }
        set(value) {
            config[comeYearKey]=value
        }
}
