package com.ma2i.water.backup

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object BackupCrypto {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_BIT_LENGTH = 128
    private const val ITERATIONS = 120_000
    private const val KEY_SIZE = 256 // bits
    private val secureRandom = SecureRandom()

    fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_SIZE)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = skf.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plain bytes and returns: base64(iv + ciphertext)
     * Caller should store salt (base64) and the returned base64 cipher text together in a wrapper JSON:
     * { "version":1, "salt":"...", "payload":"..." }
     */
    fun encryptToBase64(plain: ByteArray, key: SecretKey): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = ByteArray(IV_SIZE).also { secureRandom.nextBytes(it) }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BIT_LENGTH, iv))
        val cipherBytes = cipher.doFinal(plain)
        val result = iv + cipherBytes
        return Base64.encodeToString(result, Base64.NO_WRAP)
    }

    /**
     * Decrypt from base64(iv + ciphertext)
     * Throws exception on failure (invalid tag / wrong password).
     */
    fun decryptFromBase64(dataBase64: String, key: SecretKey): ByteArray {
        val data = Base64.decode(dataBase64, Base64.NO_WRAP)
        if (data.size < IV_SIZE) throw IllegalArgumentException("Invalid data")
        val iv = data.copyOfRange(0, IV_SIZE)
        val cipherBytes = data.copyOfRange(IV_SIZE, data.size)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BIT_LENGTH, iv))
        return cipher.doFinal(cipherBytes)
    }

    fun generateSalt(length: Int = 16): ByteArray {
        val salt = ByteArray(length)
        secureRandom.nextBytes(salt)
        return salt
    }
}
