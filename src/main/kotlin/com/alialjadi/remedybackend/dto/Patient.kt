package com.alialjadi.remedybackend.dto

import org.jetbrains.annotations.NotNull
import java.time.LocalDate
import java.util.UUID

data class PatientRequest(
    var prescriberId: UUID? = null,
    var name: String,
    var dob: String,
    var email: String,
    var phone: String,
    var password: String,
    var faceImagePath: String? = null,
)

data class PatientSummary(
    val id: UUID?,
    val name: String,
    val dob: String,
    val phone: String?
)

data class PatientIdRequest(
    val patientId: UUID,
)

data class PatientVerbose(
    val patientId: UUID,
    val prescriberId: UUID? = null,
    val patientName: String,
    val patientEmail: String,
    val patientPhone: String,
    val patientFaceImagePath: String? = null,

)

data class PhotoUploadRequest(
    val patientId: UUID,
    val photoPath: String
)



