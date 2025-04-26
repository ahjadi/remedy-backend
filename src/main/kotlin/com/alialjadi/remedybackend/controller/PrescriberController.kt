package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.authentication.UserPrincipal
import com.alialjadi.remedybackend.dto.*
import com.alialjadi.remedybackend.service.PrescriberAndBagService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/prescriber")
class PrescriberController(private val prescriberService: PrescriberAndBagService) {


    // Create new prescriber user
    // DB has unique email constraint - need to make a global exception handler to make error more expressive
    @PostMapping("/create")
    fun createNewPrescriber(@RequestBody prescriber: PrescriberRequest): ResponseEntity<Any> {
        prescriberService.createPrescriber(prescriber)
        return ResponseEntity.ok().body("Prescriber created")
    }

    // views all patients of a prescriber
    @PostMapping("/patients/list")
    fun viewMyPatients(@AuthenticationPrincipal prescriberPrincipal: UserPrincipal): ResponseEntity<Any> {

        val prescriberId =
            prescriberPrincipal.getPrescriberId() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized")
        return try {
            ResponseEntity.ok().body(prescriberService.viewPatients(prescriberId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }
    }

    //  creates a new bag for a patient
    @PostMapping("/bag/create")
    fun createNewBag(@RequestBody bagCreation: BagCreation): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.createBag(bagCreation))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    // change prescription
    @PostMapping("/bag/prescription/update")
    fun changePrescription(@RequestBody newPrescription: UpdatePrescription): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.updatePrescription(newPrescription))
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

    //    view patient and bag
    @PostMapping("/patient/bag/view")
    fun viewPatientAndBag(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body( prescriberService.viewBag(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    // Retrieve state
    @PostMapping("bag/state")
    fun getBagState(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.getState(patientId))
        } catch (e: EntityNotFoundException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))}
    }


}