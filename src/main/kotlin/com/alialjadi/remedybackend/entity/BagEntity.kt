package com.alialjadi.remedybackend.entity

import com.alialjadi.remedybackend.encryption.EncryptedStringConverter
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "bags")
data class BagEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    val patientId: UUID? = null,

    @Convert(converter = EncryptedStringConverter::class)
    var prescription: String,

    @Enumerated(EnumType.STRING)
    var state: BagState = BagState.UNSEALED,

    val createdAt: Instant = Instant.now(),
    )

enum class BagState {
    UNSEALED, SEALED, LOADED, DISPENSED, DISCARDED;

    companion object {
        fun fromDbValue(value: String): BagState {
            return valueOf(value.uppercase())
        }

        fun toDbValue(state: BagState): String {
            return state.name.lowercase()
        }}
}






