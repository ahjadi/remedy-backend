package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PullNotification
import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val patientRepository: PatientRepository,
    private val bagRepository: BagRepository,
    private val prescriberRepository: PrescriberRepository
) {

    fun getPullNotifications(): List<PullNotification> {
        val patients = patientRepository.findAll()

        return patients.map { patient ->
            val latestBag = bagRepository.findTopByPatientIdOrderByCreatedAtDesc(patient.id)
            val prescriber = patient.prescriberId?.let { prescriberRepository.findById(it).orElse(null) }

            PullNotification(
                patientId = patient.id!!,
                patientName = patient.name,
                patientDoB = patient.dob,
                patientEmail = patient.email,
                patientPhoneNumber = patient.phone,

                bagId = latestBag?.id,
                bagPrescription = latestBag?.prescription,
                bagState = latestBag?.state?.name,

                prescriberId = prescriber?.id,
                prescriberName = prescriber?.name,
                prescriberEmail = prescriber?.email,
                prescriberPhoneNumber = prescriber?.phone
            )
        }
    }
}