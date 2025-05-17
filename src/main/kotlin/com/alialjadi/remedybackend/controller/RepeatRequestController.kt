package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.entity.RepeatRequestEntity
import com.alialjadi.remedybackend.entity.RequestStatus
import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.RepeatRequestRepository
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*

@RestController
@Tag(name = "For Patients: Repeat Request API")
@RequestMapping("/api/repeat")
class RepeatRequestController(
    private val bagRepository: BagRepository,
    private val repeatRequestRepository: RepeatRequestRepository,
    private val patientRepository: PatientRepository
) {


    @PostMapping("/request")
    fun submitRepeatRequest(@RequestBody patientId: RequestDTO): ResponseEntity<String> {
        // Find patient
        val patient = patientRepository.findById(patientId.patientId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found") }

        // Find bag associated with patient
        val bag = bagRepository.findByPatientId(patientId.patientId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No bag found for patient")

        val oldRepeatRequest: RepeatRequestEntity? = repeatRequestRepository.findByBagId(bag.id!!)

        // Create or update, then save repeat request
        val repeatRequest: RepeatRequestEntity =
            oldRepeatRequest?.copy(status = RequestStatus.PENDING, requestDate = LocalDateTime.now())
                ?: RepeatRequestEntity(
                    bagId = bag.id,
                )
        repeatRequestRepository.save(repeatRequest)

        return ResponseEntity.ok("Repeat request submitted successfully.")
    }

    data class RequestDTO(val patientId: UUID)


}