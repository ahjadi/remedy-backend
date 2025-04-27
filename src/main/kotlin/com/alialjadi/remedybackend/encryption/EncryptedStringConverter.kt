package com.alialjadi.remedybackend.encryption

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component




@Component
@Converter(autoApply = false)
class EncryptedStringConverter : AttributeConverter<String, String>, ApplicationContextAware {

    companion object {
        lateinit var encryptionService: EncryptionService
            private set
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        encryptionService = applicationContext.getBean(EncryptionService::class.java)
    }

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.let { encryptionService.encrypt(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.let { encryptionService.decrypt(it) }
    }
}