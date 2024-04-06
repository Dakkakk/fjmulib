package cn.luckcc.fjmu.lib.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.ceil
import kotlin.math.floor

private fun aesEncrypt(key: ByteArray, data: ByteArray,iv: ByteArray=key): ByteArray {
    val secretKeySpec = SecretKeySpec(key, "AES")
    val ivParameterSpec = IvParameterSpec(iv)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
    return cipher.doFinal(data)
}

/**
 * referred to the function f() in aes(eone website)
 */
private fun aesKeyBuilder():String{
    val p = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    val builder = StringBuilder()
    val secureRandom = SecureRandom()
    for (t in 0 until 16){
        val o= ceil(35 * secureRandom.nextDouble()).toInt()
        builder.append(p[o])
    }
    return builder.toString()
}


/**
 * referred to the function encryptBase64() in aes(eone website)
 */
fun encryptBase64(password:String):String{
    // as the passphrase is always set to "aas-apex"
    val passphrase="aas-apex"
    val key_r = aesKeyBuilder().toByteArray()
    val key_l = MessageDigest.getInstance("MD5").digest(passphrase.toByteArray())
    val key_t = MessageDigest.getInstance("MD5").digest(key_r)
    val encKey_o = aesEncrypt(key_l, key_r)
    val ciphertext_n = aesEncrypt(key_t, password.toByteArray())
    val result_i = encKey_o + ciphertext_n
    val resultB64 = Base64.getEncoder().encodeToString(result_i);
    return resultB64
}

private fun _rds(len:Int):String{
    val _chars="ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
    val sb=StringBuilder()
    val secureRandom = SecureRandom()
    for (i in 0 until len){
        sb.append(_chars[floor(secureRandom.nextDouble()*_chars.length).toInt()])
    }
    return sb.toString()
}
private fun _gas(data:String,key0:String,iv:ByteArray):String{
    val key00=key0.replace("(^\\s+)|(\\s+\$)".toRegex(),"")
    val encrypted= aesEncrypt(key00.toByteArray(),data.toByteArray(),iv)
    val base64=Base64.getEncoder().encodeToString(encrypted)
    return base64
}
fun encryptAES(password: String, p: String):String{
    val encrypted= _gas(_rds(64)+password,p, _rds(16).toByteArray())
    return encrypted
}

