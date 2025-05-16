package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.dto.PatientIdRequest
import com.alialjadi.remedybackend.dto.PrescriberIdRequest
import com.alialjadi.remedybackend.dto.SetBagState
import com.alialjadi.remedybackend.service.NotificationService
import com.alialjadi.remedybackend.service.PatientService
import com.alialjadi.remedybackend.service.PrescriberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/device")
@Tag(name = "Device/Pull-Notification API")
class DeviceController(
    private val prescriberService: PrescriberService,
    private val patientService: PatientService,
) {


    // Retrieve state
    @Operation(
        summary = "Get bag state for a patient",
        description = "Retrieves the current state of the medication bag assigned to a specific patient by their ID"
    )
    @PostMapping("get/bag/state")
    fun getBagState(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.getState(patientId))

    }


    //    set bag state

    @Operation(
        summary = "Set new state for a bag",
        description = "MUST be in UPPERCASE. Updates the state of a medication bag (e.g., UNSEALED, SEALED, LOADED)" +
                "Quite powerful as it overrides any state and could break the machine logic"
    )
    @PostMapping("bag/set/state")
    fun setBagState(@RequestBody newState: SetBagState): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.setBagState(newState))

    }

    @Operation(
        summary = "Retrieve patient information",
        description = "Fetches the complete details of a patient by their ID"
    )
    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
    }


    @Operation(
        summary = "Retrieve prescriber information",
        description = "Returns detailed information of a prescriber using their ID"
    )
    @PostMapping("/retrieve/prescriber")
    fun retrievePrescriber(@RequestBody prescriberId: PrescriberIdRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(prescriberService.retrievePrescriber(prescriberId.prescriberId))


    }
}