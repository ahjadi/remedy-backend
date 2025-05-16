package com.alialjadi.remedybackend.controller

import com.alialjadi.remedybackend.service.NotificationService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@Tag(name = "Push Notification API")
@RequestMapping("/api/notifications")
class NotificationController(val notificationService: NotificationService) {



    @PostMapping("patient/register-token")
    fun registerTokenForPatient(@RequestBody request: TokenRequest): ResponseEntity<ApiResponse> {
       notificationService.saveFcmTokenForPatient(request.userId, request.token)
        return ResponseEntity.ok(ApiResponse(true, "FCM token registered successfully"))
    }

    @PostMapping("prescriber/register-token")
    fun registerTokenForPrescriber(@RequestBody request: TokenRequest): ResponseEntity<ApiResponse> {
        notificationService.saveFcmTokenForPrescriber(request.userId, request.token)
        return ResponseEntity.ok(ApiResponse(true, "FCM token registered successfully"))
    }

}

// DTO
data class TokenRequest(
    val userId: UUID,
    val token: String)

data class ApiResponse(
    val success: Boolean,
    val message: String
)