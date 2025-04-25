package com.alialjadi.remedybackend.authentication

import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val patientRepository: PatientRepository,
    private val prescriberRepository: PrescriberRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val patient = patientRepository.findByEmail(email)
        if (patient != null) {
            return UserPrincipal(
                id = patient.id,
                email = patient.email,
                role = "PATIENT",  // Hardcoding the role as "PATIENT
                password = patient.password,
            )
        }

        val prescriber = prescriberRepository.findByEmail(email)
        if (prescriber != null) {
            return UserPrincipal(
                id = prescriber.id,
                email = prescriber.email,
                role = "PRESCRIBER",
                password = prescriber.password
            )
        }

        throw UsernameNotFoundException("User not found with email: $email")
    }
}