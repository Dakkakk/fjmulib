package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.Serializable
import zx.dkk.utils.crypto.AESCrypto

@Serializable
class User constructor(
    val username:String,
    val passwordEncrypt:String
){
    companion object{
        private val aes=AESCrypto("#aDL-jK!C,")
        fun create(username: String,password:String)= User(username, aes.encrypt(password))
    }
    val password:String
        get() = aes.decrypt(passwordEncrypt)

}
