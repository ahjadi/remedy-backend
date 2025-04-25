package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PrescriberRequest
import com.alialjadi.remedybackend.entity.PrescriberEntity
import com.alialjadi.remedybackend.repository.PrescriberRepository
import org.postgresql.util.PasswordUtil.encodePassword
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PrescriberService(private val prescriberRepository: PrescriberRepository,
                        private val passwordEncoder: PasswordEncoder
) {

    fun createPrescriber(prescriberRequest: PrescriberRequest) {
        val newPrescriberEntity = PrescriberEntity(
            name = prescriberRequest.name,
            email = prescriberRequest.email,
            password = passwordEncoder.encode(prescriberRequest.password),
        )
        prescriberRepository.save(newPrescriberEntity)
    }

    fun updatePrescriber(prescriberRequest: PrescriberRequest) {

    }
}