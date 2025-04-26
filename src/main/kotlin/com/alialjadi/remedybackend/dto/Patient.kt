package com.alialjadi.remedybackend.dto

import java.time.LocalDate
import java.util.UUID

data class PatientRequest(
    var prescriberId: UUID? = null,
    var name: String,
    var dob: LocalDate,
    var email: String,
    var phone: String,
    var password: String,
    var faceImagePath: String? = null,
)

data class PatientSummary(
    val id: UUID?,
    val name: String,
    val dob: LocalDate,
    val phone: String?
)

data class PatientIdRequest(
    val patientId: UUID,
)

