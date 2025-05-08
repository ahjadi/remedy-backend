package com.alialjadi.remedybackend.entity

import com.alialjadi.remedybackend.encryption.EncryptedStringConverter
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
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

    val role: String = "PRESCRIBER",

    @Convert(converter = EncryptedStringConverter::class)
    @field:Pattern(
        regexp = "^[9654]\\d{7}$",
        message = "Enter a valid Kuwaiti phone number"
    )
    val phone: String,
) {
}