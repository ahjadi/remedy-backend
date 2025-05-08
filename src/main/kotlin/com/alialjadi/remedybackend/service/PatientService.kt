package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.dto.PatientVerbose
import com.alialjadi.remedybackend.dto.PhotoUploadRequest
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
    fun validateFullName(fullName: String) {
        val trimmed = fullName.trim()
        require(trimmed.isNotBlank()) { "Name cannot be blank" }
        require(trimmed.length >= 3) { "Name should be more than 2 characters" }
        require(!trimmed.any { it.isDigit() }) { "Name should not contain any digits" }
        require(trimmed.matches(Regex("^[a-zA-Z\\s'-]+$"))) {
            "Name can only contain letters, spaces, hyphens, and apostrophes"
        }
    }

    fun validatePassword(password: String) {
        require(password.length >= 8) { "Password must be at least 8 characters long" }
        require(password.any { it.isUpperCase() }) { "Password must contain at least one uppercase letter" }
        require(password.any { it.isDigit() }) { "Password must contain at least one number" }
    }

    // create new patient user
    fun createPatient(patientRequest: PatientRequest): PatientIdRequest {
        validateFullName(patientRequest.name)
        validatePassword(patientRequest.password)

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
        val patientId: UUID? = newPatientEntity.id
        return PatientIdRequest(patientId!!)
    }

    fun retrievePatient(patientId: UUID): PatientVerbose {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { EntityNotFoundException("No patient found for id $patientId") }

        return PatientVerbose(
            patientId = patient.id!!,
            prescriberId = patient.prescriberId,
            patientName = patient.name,
            patientEmail = patient.email,
            patientPhone = patient.phone,
            patientFaceImagePath = patient.faceImagePath,
        )
    }

    fun uploadPhoto(request: PhotoUploadRequest) {

        val patient = patientRepository.findById(request.patientId)
            .orElseThrow { EntityNotFoundException("No patient found for id ${request.patientId}") }

        patient.faceImagePath = request.photoPath

        patientRepository.save(patient)
    }
}