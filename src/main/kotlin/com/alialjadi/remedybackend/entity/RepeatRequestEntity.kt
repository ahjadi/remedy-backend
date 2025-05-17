package com.alialjadi.remedybackend.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "repeat_request")
data class RepeatRequestEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var bagId: UUID?,

    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,

    val requestDate: LocalDateTime = LocalDateTime.now(),

    )

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}

