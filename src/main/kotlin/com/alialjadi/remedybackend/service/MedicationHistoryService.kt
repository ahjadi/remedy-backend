package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.BagState
import com.alialjadi.remedybackend.entity.HistoryEntity
import com.alialjadi.remedybackend.repository.HistoryRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MedicationHistoryService(
    private val historyRepository: HistoryRepository
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