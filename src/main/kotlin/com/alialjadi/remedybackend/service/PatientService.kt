package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.entity.PatientEntity
import com.alialjadi.remedybackend.repository.PatientRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class PatientService(
    private val patientRepository: PatientRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // create new patient user
    fun createPatient(patientRequest: PatientRequest) {
        val newPatientEntity = PatientEntity(
            prescriberId = patientRequest.prescriberId,
            name = patientRequest.name,
            dob = patientRequest.dob,
            email = patientRequest.email,
            phone = patientRequest.phone,
            password = passwordEncoder.encode(patientRequest.password),
            faceImagePath = patientRequest.faceImagePath,
        )
        patientRepository.save(newPatientEntity)
    }

    fun retrievePatient(patientId: UUID): PatientEntity {
        return patientRepository.findById(patientId)
            .orElseThrow { EntityNotFoundException("No patient found for id $patientId") }
    }
}