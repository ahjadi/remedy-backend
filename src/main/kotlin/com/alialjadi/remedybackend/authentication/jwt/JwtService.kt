package com.alialjadi.remedybackend.authentication.jwt

import com.alialjadi.remedybackend.encryption.EncryptionService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtService(
    @Value("\${JWT_SECRET_KEY}") secretKeyBase64: String) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(secretKeyBase64)

    )
    private val expirationMs: Long = 1000 * 60 * 60 * 12 // 12 hours

    // Generate token with user ID, email, and role
    fun generateToken(id: UUID?, email: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        return Jwts.builder()
            .setSubject(email)
            .claim("id", id.toString())  // Include user ID
            .claim("role", role)          // Include user role
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    // Extract claims from the token
    private fun extractClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

    // Extract the username (email) from token
    fun extractUsername(token: String): String = extractClaims(token).subject

    // Extract user ID from token
    fun extractUserId(token: String): UUID = UUID.fromString(extractClaims(token)["id"] as String)

    // Extract a user role from token
    fun extractRole(token: String): String = extractClaims(token)["role"] as String

    // Validate if the token is valid (checks subject and expiration)
    fun isTokenValid(token: String, username: String): Boolean {
        return try {
            extractUsername(token) == username && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    // Check if the token has expired
    private fun isTokenExpired(token: String): Boolean =
        extractClaims(token).expiration.before(Date())
}

