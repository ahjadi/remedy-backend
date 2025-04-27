package com.alialjadi.remedybackend.entity

import com.alialjadi.remedybackend.encryption.EncryptedStringConverter
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "patients")
data class PatientEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    var prescriberId: UUID? = null,

    @Convert(converter = EncryptedStringConverter::class)
    var name: String,

//    @Convert(converter = EncryptedStringConverter::class)
    var dob: LocalDate,

    @Convert(converter = EncryptedStringConverter::class)
    var email: String,

    @Convert(converter = EncryptedStringConverter::class)
    var phone: String,

    var password: String,

    @Convert(converter = EncryptedStringConverter::class)
    var faceImagePath: String? = null,

    val role: String = "PATIENT"

    )