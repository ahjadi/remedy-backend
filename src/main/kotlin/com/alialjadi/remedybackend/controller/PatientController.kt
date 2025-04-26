package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PatientRequest
import com.alialjadi.remedybackend.entity.PatientEntity
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.service.PatientService
import org.postgresql.util.PSQLException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}