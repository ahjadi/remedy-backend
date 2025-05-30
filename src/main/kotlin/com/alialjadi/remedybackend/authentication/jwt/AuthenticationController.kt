package com.alialjadi.remedybackend.authentication.jwt

import com.alialjadi.remedybackend.authentication.CustomUserDetailsService
import com.alialjadi.remedybackend.authentication.UserPrincipal
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "Returns JWT Token")
class AuthenticationController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtService: JwtService
) {
    @PostMapping("/login")

    fun login(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse {
        val authToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        val authentication = authenticationManager.authenticate(authToken)

        if (authentication.isAuthenticated) {
            val userDetails = userDetailsService.loadUserByUsername(authRequest.username)

            // Ensure userDetails is of type UserPrincipal
            if (userDetails is UserPrincipal) {
                val id = userDetails.getId()
                val email = userDetails.username
                val role =
                    userDetails.authorities.first().authority.removePrefix("ROLE_")  // Extract role without "ROLE_" prefix

                // Generate JWT token with user ID, email, and role
                val token = jwtService.generateToken(id, email, role)

                return AuthenticationResponse(token)
            } else {
                throw UsernameNotFoundException("Invalid user request!")
            }
        } else {
            throw UsernameNotFoundException("Invalid user request!")
        }
    }


}

data class AuthenticationRequest(
    val username: String,
    val password: String
)

data class AuthenticationResponse(
    val token: String
)