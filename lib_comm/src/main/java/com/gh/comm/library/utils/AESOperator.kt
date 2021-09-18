package com.gh.comm.library.utils

import com.gh.mylibrary.utils.RxDataTool
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESOperator {
    companion object {
        /*
    * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
    */
        private val sKey = "RUIYUNYUEJIAYUN1"
        private val ivParameter = "RUIYUNYUEJIAYUN2"

        // 加密
        fun encrypt(sSrc: String): String {
            return try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val raw = sKey.toByteArray()
                val skeySpec = SecretKeySpec(raw, "AES")
                val iv = IvParameterSpec(ivParameter.toByteArray()) // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
                val encrypted = cipher.doFinal(sSrc.toByteArray(charset("utf-8")))
                val stringBuilder = StringBuilder("")
                for (i in encrypted.indices) {
                    val v: Int = ByteUtil.a(encrypted[i])
                    val hv = Integer.toHexString(v)
                    if (hv.length < 2) {
                        stringBuilder.append(0)
                    }
                    stringBuilder.append(hv)
                }
                stringBuilder.toString()
            } catch (e: Exception) {
                sSrc
            }
        }

        // 解密

        // 解密
        // 解密
        fun decrypt(content: String): String? {
            return if (RxDataTool.isNullString(content)) {
                null
            } else try {
                val sSrc = content.toUpperCase()
                val length = sSrc.length / 2
                val hexChars = sSrc.toCharArray()
                val encrypted1 = ByteArray(length)
                for (i in 0 until length) {
                    val pos = i * 2
                    encrypted1[i] = ByteUtil.b(charToByte(hexChars[pos]), charToByte(hexChars[pos + 1]))
                }
                val raw = sKey.toByteArray(charset("ASCII"))
                val skeySpec = SecretKeySpec(raw, "AES")
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val iv = IvParameterSpec(ivParameter.toByteArray())
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
                val original = cipher.doFinal(encrypted1)
                val originalString = ByteUtil.string(original)
                if (isMessyCode(originalString)) {
                    content
                } else originalString
            } catch (ex: Exception) {
                content
            }
        }

        private fun charToByte(c: Char): Byte {
            return "0123456789ABCDEF".indexOf(c).toByte()
        }
        /**
         * 判断字符串是否是乱码
         *
         * @param strName 字符串
         * @return 是否是乱码
         */
        fun isMessyCode(strName: String?): Boolean {
            val p = Pattern.compile("\\s*|\t*|\r*|\n*")
            val m = p.matcher(strName)
            val after = m.replaceAll("")
            val temp = after.replace("\\p{P}".toRegex(), "")
            val ch = temp.trim { it <= ' ' }.toCharArray()
            val chLength = ch.size.toFloat()
            var count = 0f
            for (i in ch.indices) {
                val c = ch[i]
                if (!Character.isLetterOrDigit(c)) {
                    if (!isChinese(c)) {
                        count += 1
                    }
                }
            }
            val result = count / chLength
            return result > 0.4
        }

        /**
         * 判断字符是否是中文
         *
         * @param c 字符
         * @return 是否是中文
         */
        fun isChinese(c: Char): Boolean {
            val ub = Character.UnicodeBlock.of(c)
            return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        }

    }

}