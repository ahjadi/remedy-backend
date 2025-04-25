package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.authentication.UserPrincipal
import com.alialjadi.remedybackend.dto.PrescriberRequest
import com.alialjadi.remedybackend.service.PrescriberService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/prescriber")
class PrescriberController(private val prescriberService: PrescriberService) {


    // Create new prescriber user
    // DB has unique email constraint - need to make a global exception handler to make error more expressive
    @PostMapping("/create")
    fun createNewPrescriber(@RequestBody prescriber: PrescriberRequest): ResponseEntity<Any> {
        prescriberService.createPrescriber(prescriber)
        return ResponseEntity.ok().body("Prescriber created")
    }

    @PostMapping("/patients/list")
    fun viewMyPatients(@AuthenticationPrincipal prescriberPrincipal: UserPrincipal): ResponseEntity<Any> {

        val prescriberId =
            prescriberPrincipal.getPrescriberId() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized")
        return try {
            ResponseEntity.ok().body(prescriberService.viewPatients(prescriberId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("${e.message}")
        }
    }


}