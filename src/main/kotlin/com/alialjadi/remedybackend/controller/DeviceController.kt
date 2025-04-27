package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PrescriberIdRequest
import com.alialjadi.remedybackend.dto.SetBagState
import com.alialjadi.remedybackend.service.PatientService
import com.alialjadi.remedybackend.service.PrescriberService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/device")
class DeviceController(private val prescriberService: PrescriberService, private val patientService: PatientService) {



    // Retrieve state
    @PostMapping("bag/state")
    fun getBagState(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.getState(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    //    set bag state
    @PostMapping("bag/set/state")
    fun setBagState(@RequestBody newState: SetBagState): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.setBagState(newState))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: UUID): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.retrievePatient(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }


    @PostMapping("/retrieve/prescriber")
    fun retrievePrescriber(@RequestBody prescriberId: PrescriberIdRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(prescriberService.retrievePrescriber(prescriberId.prescriberId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }

    }
}