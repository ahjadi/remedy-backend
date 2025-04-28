package com.alialjadi.remedybackend.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.*

@Entity
@Table(name = "prescribers")
data class PrescriberEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    var name: String,

    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Invalid email format")
    var email: String,

    var password: String,

    val role: String = "PRESCRIBER"
) {
}