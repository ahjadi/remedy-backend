package com.alialjadi.remedybackend.repository

import com.alialjadi.remedybackend.entity.BagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BagRepository : JpaRepository<BagEntity, UUID> {

    fun findByPatientId(patientId: UUID): BagEntity?
    fun findAllByPatientIdIn(patientIds: List<UUID?>): List<BagEntity>
    fun findTopByPatientIdOrderByCreatedAtDesc(patientId: UUID?): BagEntity?
}
