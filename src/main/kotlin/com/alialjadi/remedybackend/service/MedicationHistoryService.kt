package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.PrescriptionHistory
import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.BagState
import com.alialjadi.remedybackend.entity.HistoryEntity
import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.HistoryRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MedicationHistoryService(
    private val historyRepository: HistoryRepository,
    private val patientRepository: PatientRepository,
    private val prescriberRepository: PrescriberRepository

    ) {
    @Transactional
    fun recordStateChange(bag: BagEntity, newState: BagState, prescriberId: UUID?) {
        // Create history entry for the state change
        val historyEntry = HistoryEntity(
            bagId = bag.id,
            patientId = bag.patientId,
            prescriberId = prescriberId,
            action = newState,
            prescriptionCopy = bag.prescription,
            // Timestamp will be set automatically to now
        )
        historyRepository.save(historyEntry)
    }

    fun getPreviousPrescriptionRecords(patientId: UUID): List<PrescriptionHistory> {
        val listOfHistories = historyRepository.findAllByPatientId(patientId)

        val patient = patientRepository.findById(patientId)
            .orElseThrow { EntityNotFoundException("Patient not found with ID: $patientId") }

        val prescriberIds = listOfHistories.mapNotNull { it.prescriberId }.distinct()
        val prescriberMap = prescriberRepository.findAllById(prescriberIds)
            .associateBy({ it.id!! }, { it.name })

        return listOfHistories.map { history ->
            val prescriberName = history.prescriberId?.let { prescriberMap[it] } ?: "Unknown Prescriber"

            PrescriptionHistory(
                patientName = patient.name,
                dob = patient.dob,
                prescriberName = prescriberName,
                previousPrescription = history.prescriptionCopy,
                timestamp = history.timestamp
            )
        }
    }
    fun getHistoryByPrescriber(prescriberId: UUID): List<HistoryEntity> {
        return historyRepository.findByPrescriberId(prescriberId)
    }

    fun getHistoryByPatient(patientId: UUID): List<HistoryEntity> {
        return historyRepository.findByPatientId(patientId)
    }

    fun getPreviousPrescriptions(patientId: UUID): List<String> {
        // Return all previous prescriptions for this patient
        return historyRepository.findByPatientId(patientId)
            .map { it.prescriptionCopy }
            .distinct()
    }
}