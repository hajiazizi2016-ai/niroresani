package com.rasoulhajiazizi.niroresani.core.common

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

/**
 * هش امن رمز عبور با SHA-256 + Salt تصادفی.
 * الزام سند (بخش ۱۰۵۰): رمز هرگز به‌صورت متن ساده ذخیره نمی‌شود.
 * خروجی به فرمت "salt:hash" (هر دو Base64) در یک رشته ذخیره می‌شود.
 */
object PasswordHasher {

    fun hash(password: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val hash = sha256(salt + password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(salt, Base64.NO_WRAP) + ":" + Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verify(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        return try {
            val salt = Base64.decode(parts[0], Base64.NO_WRAP)
            val expectedHash = Base64.decode(parts[1], Base64.NO_WRAP)
            val actualHash = sha256(salt + password.toByteArray(Charsets.UTF_8))
            actualHash.contentEquals(expectedHash)
        } catch (e: Exception) {
            false
        }
    }

    private fun sha256(input: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(input)
    }
}
