package com.alialjadi.remedybackend.repository

import com.alialjadi.remedybackend.entity.PatientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PatientRepository : JpaRepository<PatientEntity, UUID> {
    fun findByEmail(email: String): PatientEntity?
    fun findAllByPrescriberId(prescriberId: UUID): List<PatientEntity?>
    fun findIdsByPrescriberId(prescriberId: UUID): List<UUID>
}