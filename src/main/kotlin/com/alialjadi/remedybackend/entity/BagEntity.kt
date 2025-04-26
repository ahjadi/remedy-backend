package com.alialjadi.remedybackend.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Converter
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
@Table(name = "bags")
data class BagEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    val patientId: UUID? = null,
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






