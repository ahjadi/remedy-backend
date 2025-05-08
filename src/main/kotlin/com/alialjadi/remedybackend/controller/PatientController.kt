package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.authentication.UserPrincipal
import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.dto.PhotoUploadRequest
import com.alialjadi.remedybackend.service.PatientService
import com.alialjadi.remedybackend.service.PrescriberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/patient")
@Tag(name = "Patient API")
class PatientController(
    private val patientService: PatientService,
    private val prescriberService: PrescriberService
) {


    @Operation(
        summary = "Create a new patient",
        description = "Registers a new patient. Date of birth must be in format dd-MM-yyyy. Email must be unique."
    )
    @PostMapping("/create")
    fun createNewPatient(@RequestBody patientRequest: PatientRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(patientService.createPatient(patientRequest))
    }

    @Operation(
        summary = "Upload patient photo path",
        description = "Stores the path of a patient's photo in the system"
    )
    @PostMapping("/upload/photo/path")
    fun uploadPhotoPath(
        @AuthenticationPrincipal patient: UserPrincipal,
        @RequestBody request: PhotoUploadRequest
    ): ResponseEntity<Any> {
        if (patient.getPatientId() != request.patientId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
        }
        return ResponseEntity.ok().body(patientService.uploadPhoto(request))
    }

    @Operation(
        summary = "Retrieve patient by ID",
        description = "Returns full patient details using patient ID"
    )
    @PostMapping("/retrieve/patient")
    fun retrievePatient(
        @AuthenticationPrincipal patient: UserPrincipal,
        @RequestBody patientId: PatientIdRequest
    ): ResponseEntity<Any> {
        if (patient.getPatientId() != patientId.patientId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
        }
        return ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
    }

    //    view patient and bag
    @Operation(
        summary = "View patient and their bag",
        description = "Fetches both the patient's details and their associated medication bag"
    )
    @PostMapping("/patient/bag/view")
    fun viewPatientAndBag(
        @AuthenticationPrincipal patient: UserPrincipal,
        @RequestBody patientId: PatientIdRequest
    ): ResponseEntity<Any> {
        if (patient.getPatientId() != patientId.patientId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
        }
        return ResponseEntity.ok().body(prescriberService.viewBag(patientId))
    }
}