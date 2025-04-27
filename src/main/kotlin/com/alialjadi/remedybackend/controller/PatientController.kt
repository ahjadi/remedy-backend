package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.dto.PhotoUploadRequest
import com.alialjadi.remedybackend.service.PatientService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/patient")
class PatientController(private val patientService: PatientService) {

    @PostMapping("/create")
    fun createNewPatient(@RequestBody patientRequest: PatientRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.createPatient(patientRequest))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/upload/photo/path")
    fun uploadPhotoPath(@RequestBody request: PhotoUploadRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.uploadPhoto(request))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "no patient found for id ${request.patientId}"))
        }
    }

    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }

    }
}