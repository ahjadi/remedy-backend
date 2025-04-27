package com.alialjadi.remedybackend.encryption

import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Service

@Service
class EncryptionService {
    private val encryptor: TextEncryptor

    init {

        val encryptionKey = System.getenv("ENCRYPTION_KEY")
            ?: throw IllegalStateException("ENCRYPTION_KEY environment variable must be set")
        val salt = "9f6127cf"
        encryptor = Encryptors.text(encryptionKey, salt)
    }

    fun encrypt(text: String?): String? {
        return if (text == null) null else encryptor.encrypt(text)
    }

    fun decrypt(encryptedText: String?): String? {
        return if (encryptedText == null) null else encryptor.decrypt(encryptedText)
    }
}