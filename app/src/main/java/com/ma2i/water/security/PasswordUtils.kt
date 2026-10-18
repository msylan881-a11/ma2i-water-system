package com.ma2i.water.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256 // bits
    private const val SALT_LENGTH = 16 // bytes
    private val secureRandom = SecureRandom()

    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        secureRandom.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: CharArray, salt: ByteArray): String {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = skf.generateSecret(spec).encoded
        return Base64.encodeToString(key, Base64.NO_WRAP)
    }

    fun verifyPassword(inputPassword: CharArray, storedHashBase64: String, salt: ByteArray): Boolean {
        val inputHash = hashPassword(inputPassword, salt)
        return slowEquals(inputHash.toByteArray(), storedHashBase64.toByteArray())
    }

    // Prevent timing attacks
    private fun slowEquals(a: ByteArray, b: ByteArray): Boolean {
        var diff = a.size xor b.size
        val len = minOf(a.size, b.size)
        for (i in 0 until len) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
        }
        return diff == 0
    }
}
