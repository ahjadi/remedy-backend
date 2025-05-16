package com.alialjadi.remedybackend.repository

import com.alialjadi.remedybackend.entity.RepeatRequestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RepeatRequestRepository : JpaRepository<RepeatRequestEntity, Long> {
    fun existsByBagId(bagId: UUID?): Boolean
    fun findByBagId(bagId: UUID): RepeatRequestEntity?
}