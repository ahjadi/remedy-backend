package com.alialjadi.remedybackend.service

import com.alialjadi.remedybackend.repository.BagRepository
import com.alialjadi.remedybackend.repository.PatientRepository
import com.alialjadi.remedybackend.repository.PrescriberRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import jakarta.persistence.EntityNotFoundException
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service
import java.util.UUID
import com.alialjadi.remedybackend.dto.NotificationDTO



@Service
class NotificationService(
    private val patientRepository: PatientRepository,
    private val bagRepository: BagRepository,
    private val prescriberRepository: PrescriberRepository,
    private val firebaseMessaging: FirebaseMessaging,


    ) {

    fun saveFcmTokenForPatient(userId: UUID, token: String) {
        val user = patientRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        user.fcmToken = token
        patientRepository.save(user)
    }

    fun saveFcmTokenForPrescriber(userId: UUID, token: String) {
        val user = prescriberRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        user.fcmToken = token
        prescriberRepository.save(user)
    }

    fun sendToPatient(patientId: UUID, title: String, body: String) {
        val token = patientRepository.findById(patientId).get().fcmToken ?: throw EntityNotFoundException("FCM Token not found with ID: $patientId")
        sendFcmMessage(token, title, body)
    }

    fun sendToPrescriber(prescriberId: UUID, title: String, body: String) {
        val token = prescriberRepository.findById(prescriberId).get().fcmToken ?: throw EntityNotFoundException("FCM Token not found with ID: $prescriberId")
        sendFcmMessage(token, title, body)
    }

    fun sendFcmMessage(token: String, title: String, body: String) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setToken(token)
            .setNotification(notification)
            .build()

        FirebaseMessaging.getInstance().sendAsync(message)
    }
}