package com.alialjadi.remedybackend.authentication.device

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class DeviceApiKeyFilter(
    // TODO put in env!!!
    @Value("\${REMEDY_DEVICE_API_KEY}") private val validApiKey: String
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // Only apply this filter to device API endpoints
        return !request.requestURI.startsWith("/api/device/")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip for OPTIONS requests (CORS preflight)
        if (request.method == "OPTIONS") {
            filterChain.doFilter(request, response)
            return
        }

        // Check for API key header
        val apiKey = request.getHeader("X-API-Key")

        if (apiKey == validApiKey) {
            // Set authentication with a DEVICE role
            val authorities = listOf(SimpleGrantedAuthority("ROLE_DEVICE"))
            val authentication = PreAuthenticatedAuthenticationToken("device", null, authorities)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Invalid API key\"}")
        }
    }
}