package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PrescriberRequest
import com.alialjadi.remedybackend.entity.PrescriberEntity
import com.alialjadi.remedybackend.repository.PrescriberRepository
import org.springframework.stereotype.Service

@Service
class PrescriberService(private val prescriberRepository: PrescriberRepository) {

    fun createPrescriber(prescriberRequest: PrescriberRequest) {
        val newPrescriberEntity = PrescriberEntity(
            name = prescriberRequest.name,
            email = prescriberRequest.email,
            password = prescriberRequest.password
        )
        prescriberRepository.save(newPrescriberEntity)
    }
}