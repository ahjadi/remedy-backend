package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.entity.PatientEntity
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.service.PatientService
import jakarta.persistence.EntityNotFoundException
import org.postgresql.util.PSQLException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/patient")
class PatientController( private val patientService: PatientService) {

    @PostMapping("/create")
    fun createNewPatient(@RequestBody patientRequest: PatientRequest): ResponseEntity<Any> {
       return try {
           patientService.createPatient(patientRequest)
           ResponseEntity.ok().body("Patient created successfully")
       } catch (e: Exception) {
           ResponseEntity.badRequest().body("Email already in use")
       }
    }

    // TODO THIS MUST BE DELETED | it is here JIC
    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
        } catch (e: EntityNotFoundException){
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }

    }
}