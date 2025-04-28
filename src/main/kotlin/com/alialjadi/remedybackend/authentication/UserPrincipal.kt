package com.alialjadi.remedybackend.authentication


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class UserPrincipal(
    private val id: UUID?,
    private val email: String,
    private val role: String,
    private val token: String? = null,
    private val password: String
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_$role"))
    }

    override fun getPassword(): String = password
    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun getId(): UUID? = id
    fun getJwtToken(): String? = token

    fun getPatientId(): UUID? = if (role.equals("PATIENT", ignoreCase = true)) id else null
    fun getPrescriberId(): UUID? = if (role.equals("PRESCRIBER", ignoreCase = true)) id else null
}