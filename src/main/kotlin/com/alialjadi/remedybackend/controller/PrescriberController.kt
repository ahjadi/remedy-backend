package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PrescriberRequest
import com.alialjadi.remedybackend.repository.PrescriberRepository
import com.alialjadi.remedybackend.service.PrescriberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/prescribers")
class PrescriberController (private val prescriberService: PrescriberService) {


    @PostMapping("/create")
    fun createNewPrescriber(@RequestBody prescriberRequest: PrescriberRequest) : Any {
        prescriberService.createPrescriber(prescriberRequest)
        return ResponseEntity.ok().body("Prescriber created")
    }
}