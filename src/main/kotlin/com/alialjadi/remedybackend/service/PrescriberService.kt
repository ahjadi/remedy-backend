package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PatientSummary
import com.alialjadi.remedybackend.dto.PrescriberRequest
import com.alialjadi.remedybackend.entity.PrescriberEntity
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class PrescriberService(
    private val prescriberRepository: PrescriberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val patientRepository: PatientRepository
) {

    // for prescriber
    fun createPrescriber(prescriber: PrescriberRequest) {

        val newPrescriberEntity = PrescriberEntity(
            name = prescriber.name,
            email = prescriber.email,
            password = passwordEncoder.encode(prescriber.password),
        )
        prescriberRepository.save(newPrescriberEntity)
    }

    // for prescriber
    fun viewPatients(prescriberId: UUID): List<PatientSummary> {

        prescriberRepository.findById(prescriberId)
            .orElseThrow { EntityNotFoundException("No Prescriber found with id $prescriberId") }

       return patientRepository.findAllByPrescriberId(prescriberId).map { patientEntity ->
            PatientSummary(
                id = patientEntity!!.id,
                name = patientEntity.name,
                dob = patientEntity.dob,
                phone = patientEntity.phone
            )
        }
    }

    // for prescriber
    fun assignToOtherPrescriber() {}

    // for prescriber
    fun createBag(){}

    // for prescriber
    fun updateBag(){}

    // for prescriber and device
    fun setBagState(){}

    // for prescriber
    fun viewBag(){}





}