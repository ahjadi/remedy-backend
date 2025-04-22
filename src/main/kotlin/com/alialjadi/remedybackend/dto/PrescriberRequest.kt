package com.alialjadi.remedybackend.dto

data class PrescriberRequest(
    val name: String,
    val email: String,
    val password: String,
) {
}