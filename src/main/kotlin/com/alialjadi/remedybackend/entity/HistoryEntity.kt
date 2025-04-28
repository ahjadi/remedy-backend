package com.alialjadi.remedybackend.entity

import com.alialjadi.remedybackend.encryption.EncryptedStringConverter
import jakarta.persistence.*
import java.time.Instant
import java.util.*


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

    @Convert(converter = EncryptedStringConverter::class)
    val prescriptionCopy: String,

    val timestamp: Instant = Instant.now(),

)

