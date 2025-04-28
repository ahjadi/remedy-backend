package com.alialjadi.remedybackend.entity

import jakarta.persistence.*
import java.util.*

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