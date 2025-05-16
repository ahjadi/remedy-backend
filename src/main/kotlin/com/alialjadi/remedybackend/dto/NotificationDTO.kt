package com.alialjadi.remedybackend.dto

import java.util.UUID

data class NotificationDTO(

    val patientId: UUID,
    val patientName: String,
    val patientDoB: String,
    val patientEmail: String,
    val patientPhoneNumber: String,

    val bagId: UUID? = null,
    val bagPrescription: String? = null,
    val bagState: String? = null,

    val prescriberId: UUID? = null,
    val prescriberName: String? = null,
    val prescriberEmail: String? = null,
    val prescriberPhoneNumber: String? = null,
    )


data class NotificationRequest(
    val token: String,
    val title: String,
    val body: String
)