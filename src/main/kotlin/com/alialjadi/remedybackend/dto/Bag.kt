package com.alialjadi.remedybackend.dto

import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.BagState
import com.alialjadi.remedybackend.entity.PatientEntity
import java.util.*

data class BagCreation(
    val patientId: UUID,
    val prescription: String
)

data class UpdatePrescription(
    val patientId: UUID,
    val prescription: String
)

data class SetBagState(
    val patientId: UUID,
    val prescriberId: UUID,
    val bagState: BagState
)



data class PatientBagState(
    val bagState: BagState
)

data class PatientAndTheirBagSummary(
    // Patient Data
    val name: String,
    val dob: String,
    val email: String,
    val phone: String,

    // Bag Data
    val prescription: String,
    val bagState: BagState,
)

data class PatientsAndTheirBagsVerbose(
    // Patient Data
    val patient: PatientEntity,
    // Bag Data
    val bag: BagEntity

)