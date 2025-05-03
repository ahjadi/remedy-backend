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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeParseException

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
        return try {
            ResponseEntity.ok().body(patientService.createPatient(patientRequest))
        } catch (e: DateTimeParseException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body("Check if Email already in use and in proper format | DoB format: dd-MM-yyyy")
        }
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
        return try {
            if (patient.getPatientId() != request.patientId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
            }
            ResponseEntity.ok().body(patientService.uploadPhoto(request))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "no patient found for id ${request.patientId}"))
        }
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
        return try {
            if (patient.getPatientId() != patientId.patientId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
            }
            ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }

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
        return try {
            if (patient.getPatientId() != patientId.patientId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient Id does not match request.")
            }
            ResponseEntity.ok().body(prescriberService.viewBag(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }
}