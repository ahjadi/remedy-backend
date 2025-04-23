package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.entity.PatientEntity
import com.alialjadi.remedybackend.repository.PatientRepository
import org.springframework.stereotype.Service

@Service
class PatientService(private val patientRepository: PatientRepository) {

    // create new patient user
    fun createPatient(patientRequest: PatientRequest) {
        val newPatientEntity = PatientEntity(
            prescriberId = patientRequest.prescriberId,
            name = patientRequest.name,
            dob = patientRequest.dob,
            email = patientRequest.email,
            phone =  patientRequest.phone,
            password = patientRequest.password,
            faceImagePath = patientRequest.faceImagePath,
        )
        patientRepository.save(newPatientEntity)
    }
}