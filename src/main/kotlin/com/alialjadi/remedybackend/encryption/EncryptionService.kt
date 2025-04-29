package com.alialjadi.remedybackend.encryption

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Service

@Service
class EncryptionService(
    @Value("\${ENCRYPTION_KEY}")
    private val encryptionKey: String,

    @Value("\${ENCRYPTION_SALT}")
    private val salt: String,
) {
    private val encryptor: TextEncryptor = Encryptors.text(encryptionKey, salt)

    fun encrypt(text: String?): String? {
        return if (text == null) null else encryptor.encrypt(text)
    }

    fun decrypt(encryptedText: String?): String? {
        return if (encryptedText == null) null else encryptor.decrypt(encryptedText)
    }
}