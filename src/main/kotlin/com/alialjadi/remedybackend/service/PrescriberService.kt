package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.*
import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.BagState
import com.alialjadi.remedybackend.entity.PrescriberEntity
import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Async
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class PrescriberService(
    private val prescriberRepository: PrescriberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val patientRepository: PatientRepository,
    private val bagRepository: BagRepository,
    private val historyService: MedicationHistoryService,
    private val notificationService: NotificationService,
) {

    fun validateFullName(fullName: String) {
        val trimmed = fullName.trim()
        require(trimmed.isNotBlank()) { "Name cannot be blank" }
        require(trimmed.length >= 3) { "Name should be more than 2 characters" }
        require(!trimmed.any { it.isDigit() }) { "Name should not contain any digits" }
        require(trimmed.matches(Regex("^[a-zA-Z\\s'-]+$"))) {
            "Name can only contain letters, spaces, hyphens, and apostrophes"
        }
    }

    fun validatePassword(password: String) {
        require(password.length >= 8) { "Password must be at least 8 characters long" }
        require(password.any { it.isUpperCase() }) { "Password must contain at least one uppercase letter" }
        require(password.any { it.isDigit() }) { "Password must contain at least one number" }
    }

    fun deletePatient(patientId: UUID): String {
        if(!patientRepository.existsById(patientId)){
            throw EntityNotFoundException("Patient with id $patientId not found")
        }
        patientRepository.deleteById(patientId)
        return "Patient deleted successfully"
    }

        // Retrieve all bags assigned to a prescriber == A list of bags
        fun retrieveAllBags(prescriberId: UUID?): List<BagEntity> {

            val patientIds = patientRepository.findAllByPrescriberId(prescriberId!!).map { it!!.id }
            val bags = bagRepository.findAllByPatientIdIn(patientIds)

            return bags
        }

        // Retrieve all unsealed bags assigned to a prescriber == A list of bags
        fun retrieveAllUnsealedBags(prescriberId: UUID?): List<BagEntity> {

            val patientIds = patientRepository.findAllByPrescriberId(prescriberId!!).map { it!!.id }
            val unsealedBags = bagRepository.findAllByPatientIdIn(patientIds).filter { it.state == BagState.UNSEALED }
            return unsealedBags
        }

    fun retrieveAllUnsealedBagsV2(prescriberId: UUID?): List<PatientsAndTheirBagsVerbose> {
        val patients = patientRepository.findAllByPrescriberId(prescriberId!!)
        val patientMap = patients.associateBy { it?.id }

        val unsealedBags = bagRepository
            .findAllByPatientIdIn(patientMap.keys.toList())
            .filter { it.state == BagState.UNSEALED }

        return unsealedBags.mapNotNull { bag ->
            val patient = patientMap[bag.patientId]
            if (patient != null) PatientsAndTheirBagsVerbose(patient, bag) else null
        }
    }

    fun retrieveAllPatientsAndBags(prescriberId: UUID): List<PatientsAndTheirBagsVerbose> {
        val patients = patientRepository.findAllByPrescriberId(prescriberId)
        val patientMap = patients.associateBy { it?.id }

        val bags = bagRepository.findAllByPatientIdIn(patientMap.keys.toList())

        return bags.mapNotNull { bag ->
            val patient = patientMap[bag.patientId]
            if (patient != null) PatientsAndTheirBagsVerbose(patient, bag) else null
        }
    }


        // for prescriber
        fun createPrescriber(prescriber: PrescriberRequest) {
            validateFullName(prescriber.name)
            validatePassword(prescriber.password)

            val newPrescriberEntity = PrescriberEntity(
                name = prescriber.name,
                email = prescriber.email.lowercase(),
                password = passwordEncoder.encode(prescriber.password),
                phone = prescriber.phone,
            )
            prescriberRepository.save(newPrescriberEntity)
        }




        // for prescriber
        fun viewPatients(prescriberId: UUID): List<PatientSummary> {

            prescriberRepository.findById(prescriberId)
                .orElseThrow { EntityNotFoundException("No Prescriber found with id $prescriberId") }

            return patientRepository.findAllByPrescriberId(prescriberId).map { patientEntity ->
                PatientSummary(
                    id = patientEntity!!.id,
                    name = patientEntity.name,
                    dob = patientEntity.dob,
                    phone = patientEntity.phone
                )
            }
        }

        // for prescriber: creates or update a new bag for a patient
        fun createBag(bag: BagCreation) {

            patientRepository.findById(bag.patientId)
                .orElseThrow { EntityNotFoundException("No patient found for id ${bag.patientId}") }

            val existingBag = bagRepository.findByPatientId(bag.patientId)

            val bagToSave = existingBag?.copy(prescription = bag.prescription, state = BagState.UNSEALED)
                ?: BagEntity(patientId = bag.patientId, prescription = bag.prescription)

            bagRepository.save(bagToSave)
        }

        // for prescriber: updates the prescription only no effect on its state
        fun updatePrescription(newPrescription: UpdatePrescription) {

            val bag = bagRepository.findByPatientId(newPrescription.patientId)
                ?: throw EntityNotFoundException("No bag found for patient id ${newPrescription.patientId}")


            bag.prescription = newPrescription.prescription
            bag.state = BagState.UNSEALED

            bagRepository.save(bag)
        }

        // for prescriber: updates the state of the bag
        @Transactional
        fun setBagState(newBagState: SetBagState) {
            val bag = bagRepository.findByPatientId(newBagState.patientId)
                ?: throw EntityNotFoundException("No bag found for patient id ${newBagState.patientId}")

            val originalState = bag.state

            // Only check repeat status when trying to change to UNSEALED
            if (newBagState.bagState == BagState.UNSEALED && !bag.isRepeat) {
                throw IllegalStateException("This bag is not eligible for repeat prescriptions and cannot be unsealed again.")
            }

            bag.state = newBagState.bagState
            val updatedBag = bagRepository.save(bag)

            if (originalState != newBagState.bagState) {
                historyService.recordStateChange(updatedBag, newBagState.bagState, newBagState.prescriberId)

                val prescriberId = patientRepository.findById(newBagState.patientId)
                    .orElseThrow { EntityNotFoundException("No patient found for id ${newBagState.patientId}") }
                    .prescriberId ?: throw EntityNotFoundException("No prescriber assigned to patient ${newBagState.patientId}")

                handleStateChangeNotification(updatedBag, prescriberId)
            }
        }
    @Transactional
    fun setBagStateDevice(newBagState: DeviceSetBagState) {
        val bag = bagRepository.findByPatientId(newBagState.patientId)
            ?: throw EntityNotFoundException("No bag found for patient id ${newBagState.patientId}")

        val originalState = bag.state

        // Only check repeat status when trying to change to UNSEALED
        if (newBagState.bagState == BagState.UNSEALED && !bag.isRepeat) {
            throw IllegalStateException("This bag is not eligible for repeat prescriptions and cannot be unsealed again.")
        }

        bag.state = newBagState.bagState
        val updatedBag = bagRepository.save(bag)

        val prescriberId = patientRepository.findById(newBagState.patientId)
            .orElseThrow { EntityNotFoundException("No patient found for id ${newBagState.patientId}") }
            .prescriberId ?: throw EntityNotFoundException("No prescriber assigned to patient ${newBagState.patientId}")

        if (originalState != newBagState.bagState) {
            historyService.recordStateChange(updatedBag, newBagState.bagState, prescriberId)

            handleStateChangeNotification(updatedBag, prescriberId)
        }
    }

    fun handleStateChangeNotification(bag: BagEntity, prescriberId: UUID) {
        val patientName = patientRepository.findById(bag.patientId!!).get().name.substringBefore(" ")
        when (bag.state) {
            BagState.SEALED -> notificationService.sendToPatient(
                bag.patientId!!,
                "Hello $patientName!",
                "Remedy is preparing your prescription..."
            )
            BagState.LOADED -> {
                notificationService.sendToPatient(
                    bag.patientId!!,
                    "Remedy is Ready!",
                    "Your prescription [${bag.prescription}] is ready for pickup. Please pick it up within 24hrs."
                )
                checkLoadedDuration(bag, prescriberId)
            }
            BagState.DISCARDED -> notificationService.sendToPatient(
                bag.patientId!!,
                "You missed it :(",
                "Your prescription [${bag.prescription}] has been removed from Remedy."
            )
            BagState.DISPENSED -> notificationService.sendToPrescriber(
                prescriberId,
                "Prescription Picked Up",
                "Patient [with ID: ${bag.patientId}] has picked up their prescription [${bag.prescription}]."
            )
            else -> {}
        }
    }
    @Async
    fun checkLoadedDuration(bag: BagEntity, prescriberId: UUID) {
        GlobalScope.launch {
            delay(24 * 60 * 60 * 1000) // 24 hours
            val latestBag = bagRepository.findById(bag.id!!).orElse(null)
            if (latestBag?.state == BagState.LOADED) {
                notificationService.sendToPrescriber(
                    prescriberId,
                    "Prescription Expired",
                    "Please remove prescription for patient ID: ${bag.patientId}"
                )
            }
        }
    }


        // for prescriber: returns info of patient and their bag
        fun viewBag(patientId: PatientIdRequest): PatientAndTheirBagSummary {

            val patient = patientRepository.findById(patientId.patientId)
                .orElseThrow { EntityNotFoundException("No patient found for id $patientId") }

            val bag = bagRepository.findByPatientId(patientId.patientId)
                ?: throw EntityNotFoundException("No bag found for patient id $patientId")

            return PatientAndTheirBagSummary(
                name = patient.name,
                dob = patient.dob,
                email = patient.email,
                phone = patient.phone,

                prescription = bag.prescription,
                bagState = bag.state
            )

        }

        // for prescriber and device: retrieve BAG STATE
        fun getState(patientId: PatientIdRequest): PatientBagState {
            val bag = bagRepository.findByPatientId(patientId.patientId)
                ?: throw EntityNotFoundException("No bag found for patientId $patientId")
            return PatientBagState(bag.state)
        }

        // retrieve all unassigned patients
        fun retrieveUnassignedPatients(): List<Any> {
            val unassignedPatients = patientRepository.findAll().filter { it.prescriberId == null }
                .map { patientEntity ->
                    PatientVerbose(
                        patientId = patientEntity.id!!,
                        prescriberId = null,
                        patientName = patientEntity.name,
                        patientEmail = patientEntity.email,
                        patientPhone = patientEntity.phone,
                        patientFaceImagePath = patientEntity.faceImagePath,
                    )
                }
            return unassignedPatients.ifEmpty {
                listOf("No unassigned patients")
            }
        }


        // for prescriber
        fun assignPatientToPrescriber(prescriberToPatient: AssignPrescriber) {

            val prescriber = prescriberRepository.findById(prescriberToPatient.prescriberId)
                .orElseThrow { EntityNotFoundException("No prescriber found for ${prescriberToPatient.prescriberId}") }
            val patient = patientRepository.findById(prescriberToPatient.patientId)
                .orElseThrow { EntityNotFoundException("No patient found for ${prescriberToPatient.patientId}") }

            patient.prescriberId = prescriberToPatient.prescriberId
            patientRepository.save(patient)
        }

        fun retrievePrescriber(prescriberId: UUID): PrescriberVerbose? {
            val prescriber = prescriberRepository.findById(prescriberId)
                .orElseThrow { EntityNotFoundException("No Prescriber found for id $prescriberId") }

            val prescriberData = PrescriberVerbose(
                prescriberId = prescriber.id!!,
                prescriberName = prescriber.name,
                prescriberEmail = prescriber.email,
            )

            return prescriberData
        }
    }