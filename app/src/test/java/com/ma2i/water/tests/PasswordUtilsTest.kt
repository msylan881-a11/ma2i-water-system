package com.ma2i.water.tests

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Base64

class PasswordUtilsTest {

    @Test
    fun hashAndVerify_samePassword() {
        val password = "testPass123".toCharArray()
        val salt = com.ma2i.water.security.PasswordUtils.generateSalt()
        val hash = com.ma2i.water.security.PasswordUtils.hashPassword(password, salt)
        val verified = com.ma2i.water.security.PasswordUtils.verifyPassword(password, hash, salt)
        assertEquals(true, verified)
    }

    @Test
    fun hash_differentSaltsProduceDifferentHashes() {
        val password = "password".toCharArray()
        val salt1 = com.ma2i.water.security.PasswordUtils.generateSalt()
        val salt2 = com.ma2i.water.security.PasswordUtils.generateSalt()
        val hash1 = com.ma2i.water.security.PasswordUtils.hashPassword(password, salt1)
        val hash2 = com.ma2i.water.security.PasswordUtils.hashPassword(password, salt2)
        assertEquals(false, hash1 == hash2)
    }
}
