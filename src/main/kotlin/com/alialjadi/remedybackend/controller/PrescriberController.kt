package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.authentication.UserPrincipal
import com.alialjadi.remedybackend.dto.*
import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.service.MedicationHistoryService
import com.alialjadi.remedybackend.service.PatientService
import com.alialjadi.remedybackend.service.PrescriberService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/prescriber")
class PrescriberController(
    private val prescriberService: PrescriberService,
    private val historyService: MedicationHistoryService,
    private val patientService: PatientService,
) {

    // TODO make endpoint for each entity just in case. make an endpoint for recurring prescription
    // Create new prescriber user
    // DB has unique email constraint - need to make a global exception handler to make error more expressive
    @PostMapping("/create")
    fun createNewPrescriber(@RequestBody prescriber: PrescriberRequest): ResponseEntity<Any> {
        return try {
            prescriberService.createPrescriber(prescriber)
            ResponseEntity.ok().body("Prescriber created")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Email already in use or ${e.message}, email should be in proper format")
        }
    }

    @PostMapping("/patient/assign")
    fun assignPrescriber(@RequestBody assignedPrescriber: AssignPrescriber): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok()
                .body(prescriberService.assignPatientToPrescriber(assignedPrescriber))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "${e.message}"))
        }
    }

    // Retrieve all bags for a specific prescriber
    @GetMapping("/patient/bags")
    fun retrieveAllBags(@AuthenticationPrincipal principalId: UserPrincipal): List<BagEntity> {
        return prescriberService.retrieveAllBags(principalId.getPrescriberId())
    }

    // Retrieve all unsealed bags for a specific prescriber
    @GetMapping("/patient/unsealed/bags")
    fun retrieveAllUnsealedBags(@AuthenticationPrincipal principalId: UserPrincipal): List<BagEntity> {
        return prescriberService.retrieveAllUnsealedBags(principalId.getPrescriberId())
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
    @PostMapping("/bag/set/state")
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
            ResponseEntity.ok().body(prescriberService.viewBag(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }

    // Retrieve state
    @PostMapping("/bag/state")
    fun getBagState(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {

        return try {
            ResponseEntity.ok().body(prescriberService.getState(patientId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        }
    }


    @PostMapping("/patient/prescriptions/history")
    fun getPatientPrescriptionHistory(@RequestBody patientId: PatientIdRequest): Any {

        return ResponseEntity.ok().body(historyService.getPreviousPrescriptionRecords(patientId.patientId))
    }

    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))
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