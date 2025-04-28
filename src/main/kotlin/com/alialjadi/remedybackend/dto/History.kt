package com.alialjadi.remedybackend.dto

import java.time.Instant

data class PrescriptionHistory(
    val patientName: String,
    val dob: String,
    val prescriberName: String,
    val previousPrescription: String,
    val timestamp: Instant,
)