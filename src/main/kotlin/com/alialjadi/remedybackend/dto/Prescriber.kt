package com.alialjadi.remedybackend.dto

import java.util.*

data class PrescriberRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
)
data class AssignPrescriber(
    val patientId: UUID,
    val prescriberId: UUID,
)

data class PrescriberIdRequest(
    val prescriberId: UUID,
)

data class PrescriberVerbose(
    val prescriberId: UUID,
    val prescriberName: String,
    val prescriberEmail: String,

)
