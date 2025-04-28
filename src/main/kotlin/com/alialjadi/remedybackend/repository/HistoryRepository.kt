package com.alialjadi.remedybackend.repository

import com.alialjadi.remedybackend.entity.HistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HistoryRepository : JpaRepository<HistoryEntity, UUID> {
    fun findByPrescriberId(prescriberId: UUID): List<HistoryEntity>
    fun findByPatientId(patientId: UUID): List<HistoryEntity>
    fun findAllByPatientId(patientId: UUID): List<HistoryEntity>
}