package com.alialjadi.remedybackend.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "prescribers")
data class PrescriberEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    var name: String,
    var email: String,
    var password: String,

    val role: String = "PRESCRIBER"
) {
}