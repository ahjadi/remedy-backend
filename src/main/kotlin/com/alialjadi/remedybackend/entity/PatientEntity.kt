package com.alialjadi.remedybackend.entity

import com.alialjadi.remedybackend.encryption.EncryptedStringConverter
import com.alialjadi.remedybackend.service.DateTimeValid
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.util.*

@Entity
@Table(name = "patients")
data class PatientEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    var prescriberId: UUID? = null,

    @Convert(converter = EncryptedStringConverter::class)
    var name: String,

    @Convert(converter = EncryptedStringConverter::class)
    @field:DateTimeValid(format = "dd-MM-yyyy", message = "dd-MM-yyyy")
    var dob: String,


    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Invalid email format")
    var email: String,

    @Convert(converter = EncryptedStringConverter::class)
    @field:Pattern(
        regexp = "^[9654]\\d{7}$",
        message = "Enter a valid Kuwaiti phone number"
    )
    var phone: String,

    var password: String,

    @Convert(converter = EncryptedStringConverter::class)
    var faceImagePath: String? = null,

    val role: String = "PATIENT"

)

