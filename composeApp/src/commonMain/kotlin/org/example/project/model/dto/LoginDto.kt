package org.example.project.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val username: String? = null,
    val password: String? = null,
)