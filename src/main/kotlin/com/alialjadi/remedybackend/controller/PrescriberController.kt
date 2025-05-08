package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.authentication.UserPrincipal
import com.alialjadi.remedybackend.dto.*
import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.BagState
import com.alialjadi.remedybackend.service.MedicationHistoryService
import com.alialjadi.remedybackend.service.PatientService
import com.alialjadi.remedybackend.service.PrescriberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/prescriber")
@Tag(name = "Prescriber API", description = "Operations related to prescribers and their assigned patients and bags")

class PrescriberController(
    private val prescriberService: PrescriberService,
    private val historyService: MedicationHistoryService,
    private val patientService: PatientService,
) {

    @Operation(summary = "Delete a patient by ID", description = "Deletes a patient if the patient ID exists.")
    @DeleteMapping("/remove/{patientId}")
    fun removePatient(@PathVariable("patientId") patientId: UUID): ResponseEntity<Any> {
        return ResponseEntity.ok().body(prescriberService.deletePatient(patientId))
    }

    @Operation(summary = "Returns a list of unassigned patients.")
    @GetMapping("/unassigned/patients")
    fun retrieveAllUnassignedPatients(): ResponseEntity<Any> {
        val unassignedPatients = prescriberService.retrieveUnassignedPatients()

        return ResponseEntity.ok().body(prescriberService.retrieveUnassignedPatients())
    }

    // Create new prescriber user
    @Operation(summary = "Create a new prescriber", description = "Registers a new prescriber using their details")
    @PostMapping("/create")
    fun createNewPrescriber(@RequestBody prescriber: PrescriberRequest): ResponseEntity<Any> {
        prescriberService.createPrescriber(prescriber)
        return ResponseEntity.ok().body("Prescriber created")
    }

    @Operation(summary = "Assign patient to prescriber", description = "Links a patient with a specific prescriber")
    @PostMapping("/patient/assign")
    fun assignPrescriber(@RequestBody assignedPrescriber: AssignPrescriber): ResponseEntity<Any> {

        return ResponseEntity.ok()
            .body(prescriberService.assignPatientToPrescriber(assignedPrescriber))
    }

    // Retrieve all bags for a specific prescriber
    @Operation(
        summary = "Get all bags for prescriber's patients",
        description = "Retrieves all bags associated with a prescriber's patients"
    )
    @GetMapping("/patient/bags")
    fun retrieveAllBags(@AuthenticationPrincipal principalId: UserPrincipal): List<BagEntity> {
        // TODO This is not optimal I should filter at the DB level later
        return prescriberService.retrieveAllBags(principalId.getPrescriberId())
    }

    @Operation(
        summary = "Get all bags for prescriber's patients",
        description = "Returns List<PatientsAndTheirBagsVerbose>"
    )
    @GetMapping("/patient/v2/bags")
    fun retrieveAllV2Bags(@AuthenticationPrincipal principal: UserPrincipal): ResponseEntity<List<PatientsAndTheirBagsVerbose>?> {
        return ResponseEntity.ok()
            .body(prescriberService.retrieveAllPatientsAndBags(principal.getPrescriberId()!!))
    }


    @Operation(
        summary = "Get LOADED bags only",
        description = "Retrieves bags in LOADED state only for a prescriber's patients"
    )
    @GetMapping("/patient/loaded/bags")
    fun retrieveLoadedBags(@AuthenticationPrincipal principalId: UserPrincipal): List<BagEntity> {
        // This is not optimal I should filter at the DB level later
        return prescriberService.retrieveAllBags(principalId.getPrescriberId())
            .filter { it.state == BagState.LOADED }
    }

    @Operation(
        summary = "Get Loaded bags only for prescriber's patients",
        description = "Returns List<PatientsAndTheirBagsVerbose>"
    )
    @GetMapping("/patient/v2/loaded/bags")
    fun retrieveLoadedBagsV2(@AuthenticationPrincipal principal: UserPrincipal): List<PatientsAndTheirBagsVerbose> {
        return prescriberService.retrieveAllPatientsAndBags(principal.getPrescriberId()!!)
            .filter { (patient, bag) -> bag.state == BagState.LOADED }
    }


    @Operation(
        summary = "Get UNSEALED bags only",
        description = "Retrieves bags in UNSEALED state only for a prescriber's patients"
    )
    // Retrieve all unsealed bags for a specific prescriber
    @GetMapping("/patient/unsealed/bags")
    fun retrieveAllUnsealedBags(@AuthenticationPrincipal principalId: UserPrincipal): List<BagEntity> {
        return prescriberService.retrieveAllUnsealedBags(principalId.getPrescriberId())
    }


    @Operation(
        summary = "Get UNSEALED bags only",
        description = "returns List<PatientsAndTheirBagsVerbose> in UNSEALED state only for a prescriber's patients"
    )
    @GetMapping("/patient/v2/unsealed/bags")
        fun retrieveAllUnsealedBagsV2(@AuthenticationPrincipal principal: UserPrincipal): List<PatientsAndTheirBagsVerbose> {
            return prescriberService.retrieveAllUnsealedBagsV2(principal.getPrescriberId()!!)
        }

    // views all patients of a prescriber
    @Operation(
        summary = "List all assigned patients",
        description = "Returns all patients assigned to the authenticated (By JWT) prescriber"
    )
    @PostMapping("/patients/list")
    fun viewMyPatients(@AuthenticationPrincipal prescriberPrincipal: UserPrincipal): ResponseEntity<Any> {

        val prescriberId =
            prescriberPrincipal.getPrescriberId() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized")
        return ResponseEntity.ok().body(prescriberService.viewPatients(prescriberId))
    }


    //  creates a new bag for a patient
    @Operation(summary = "Create new bag", description = "Creates a new medication bag for a patient")
    @PostMapping("/bag/create")
    fun createNewBag(@RequestBody bagCreation: BagCreation): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.createBag(bagCreation))
    }

    // change prescription
    @Operation(summary = "Update prescription", description = "Modifies the prescription for a specific bag")
    @PostMapping("/bag/prescription/update")
    fun changePrescription(@RequestBody newPrescription: UpdatePrescription): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.updatePrescription(newPrescription))

    }

    //    set bag state
    @Operation(
        summary = "Set bag state", description = "Sets the state of a bag (e.g., SEALED, UNSEALED)" +
                "Quite powerful as it overrides any state and could break machine logic"
    )
    @PostMapping("/bag/set/state")
    fun setBagState(@RequestBody newState: SetBagState): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.setBagState(newState))

    }

    //    view patient and bag
    @Operation(summary = "View patient and bag", description = "Returns patient and associated bag details")
    @PostMapping("/patient/bag/view")
    fun viewPatientAndBag(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(prescriberService.viewBag(patientId))

    }

    // Retrieve state
    @Operation(summary = "Get bag state", description = "Returns the current state of a bag")
    @PostMapping("/bag/state")
    fun getBagState(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {

        return ResponseEntity.ok().body(prescriberService.getState(patientId))

    }


    @Operation(
        summary = "Get patient prescription history",
        description = "Returns all previous prescriptions for a patient"
    )
    @PostMapping("/patient/prescriptions/history")
    fun getPatientPrescriptionHistory(@RequestBody patientId: PatientIdRequest): Any {

        return ResponseEntity.ok().body(historyService.getPreviousPrescriptionRecords(patientId.patientId))
    }

    @Operation(summary = "Retrieve patient by ID", description = "Fetches full details for a patient by their ID")
    @PostMapping("/retrieve/patient")
    fun retrievePatient(@RequestBody patientId: PatientIdRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(patientService.retrievePatient(patientId.patientId))

    }

    @Operation(summary = "Retrieve prescriber by ID", description = "Fetches full details for a prescriber by their ID")
    @PostMapping("/retrieve/prescriber")
    fun retrievePrescriber(@RequestBody prescriberId: PrescriberIdRequest): ResponseEntity<Any> {
        return ResponseEntity.ok().body(prescriberService.retrievePrescriber(prescriberId.prescriberId))


    }
}