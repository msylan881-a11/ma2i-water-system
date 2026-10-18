package com.ma2i.water.tests

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class BackupCryptoTest {

    @Test
    fun encryptDecrypt_roundTrip() {
        val plain = "{\"hello\":\"world\"}".toByteArray(Charsets.UTF_8)
        val password = "backup-pass".toCharArray()
        val salt = com.ma2i.water.backup.BackupCrypto.generateSalt()
        val key = com.ma2i.water.backup.BackupCrypto.deriveKey(password, salt)
        val payload = com.ma2i.water.backup.BackupCrypto.encryptToBase64(plain, key)
        val decrypted = com.ma2i.water.backup.BackupCrypto.decryptFromBase64(payload, key)
        assertArrayEquals(plain, decrypted)
    }
}
