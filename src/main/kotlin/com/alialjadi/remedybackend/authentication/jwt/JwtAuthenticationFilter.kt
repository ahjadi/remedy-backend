package com.alialjadi.remedybackend.authentication.jwt

import com.alialjadi.remedybackend.authentication.CustomUserDetailsService
import com.alialjadi.remedybackend.authentication.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        val email = jwtService.extractUsername(token)

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtService.isTokenValid(token, email)) {
                val userDetails = userDetailsService.loadUserByUsername(email)

                // If you're using a custom UserPrincipal and want to attach the token:
                val userPrincipal = if (userDetails is UserPrincipal) {
                    UserPrincipal(
                        id = userDetails.getId(),
                        email = userDetails.username,
                        role = userDetails.authorities.first().authority.removePrefix("ROLE_"),
                        token = token,
                        password = userDetails.password,
                    )
                } else {
                    userDetails
                }

                val authToken = UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}