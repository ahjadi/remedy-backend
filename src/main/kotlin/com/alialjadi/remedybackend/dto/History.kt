package com.alialjadi.remedybackend.dto

import java.time.Instant
import java.time.LocalDate

data class PrescriptionHistory(
    val patientName: String,
    val dob: LocalDate,
    val prescriberName: String,
    val previousPrescription: String,
    val timestamp: Instant,
)