package com.alialjadi.remedybackend.repository

import com.alialjadi.remedybackend.entity.PrescriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PrescriberRepository : JpaRepository<PrescriberEntity, UUID>{
    fun findByEmail(email: String): PrescriberEntity?
}