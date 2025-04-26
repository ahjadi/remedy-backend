package com.alialjadi.remedybackend.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID


@Entity
@Table(name = "medication_history")
data class HistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    val bagId: UUID? = null,
    val patientId: UUID? = null,
    val prescriberId: UUID? = null,

    @Enumerated(EnumType.STRING)
    val action: BagState,

    val prescriptionCopy: String,

    val timestamp: Instant = Instant.now(),

)

