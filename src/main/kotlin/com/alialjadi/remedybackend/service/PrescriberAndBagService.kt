package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.dto.*
import com.alialjadi.remedybackend.entity.BagEntity
import com.alialjadi.remedybackend.entity.PrescriberEntity
import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class PrescriberAndBagService(
    private val prescriberRepository: PrescriberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val patientRepository: PatientRepository,
    private val bagRepository: BagRepository,
) {

    // for prescriber
    fun createPrescriber(prescriber: PrescriberRequest) {

        val newPrescriberEntity = PrescriberEntity(
            name = prescriber.name,
            email = prescriber.email,
            password = passwordEncoder.encode(prescriber.password),
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

    // for prescriber: creates a new bag for a patient
    fun createBag(bag: BagCreation) {

        patientRepository.findById(bag.patientId)
            .orElseThrow { EntityNotFoundException("No patient found for id ${bag.patientId}") }

        val newBagEntity = BagEntity(
            patientId = bag.patientId,
            prescription = bag.prescription,
        )
        bagRepository.save(newBagEntity)
    }

    // for prescriber: updates the prescription only no effect on its state
    fun updatePrescription(newPrescription: UpdatePrescription) {

        val bag = bagRepository.findByPatientId(newPrescription.patientId)
            ?: throw EntityNotFoundException("No bag found for patient id ${newPrescription.patientId}")

        bag.prescription = newPrescription.prescription

        bagRepository.save(bag)
    }

    // for prescriber and device: updates the state of the bag
    fun setBagState(newBageState: SetBagState) {

        val bag = bagRepository.findByPatientId(newBageState.patientId)
            ?: throw EntityNotFoundException("No bag found for patient id ${newBageState.patientId}")

        bag.state = newBageState.bagState
        bagRepository.save(bag)

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

    // for prescriber TODO
    fun assignPatientToPrescriber(prescriberToPatient: AssignPrescriber) {

        val prescriber = prescriberRepository.findById(prescriberToPatient.prescriberId)
            .orElseThrow { EntityNotFoundException("No prescriber found for ${prescriberToPatient.prescriberId}") }
        val patient = patientRepository.findById(prescriberToPatient.patientId)
            .orElseThrow { EntityNotFoundException("No patient found for ${prescriberToPatient.patientId}") }

        patient.prescriberId = prescriberToPatient.prescriberId
        patientRepository.save(patient)
    }


}