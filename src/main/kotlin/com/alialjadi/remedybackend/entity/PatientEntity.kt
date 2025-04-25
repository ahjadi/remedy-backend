package com.alialjadi.remedybackend.entity

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

    var name: String,
    var dob: LocalDate,
    var email: String,
    var phone: String,
    var password: String,
    var faceImagePath: String? = null,

    val role: String = "PATIENT"

    )