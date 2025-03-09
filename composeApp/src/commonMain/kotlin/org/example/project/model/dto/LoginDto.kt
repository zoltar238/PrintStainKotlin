package org.example.project.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class LoginDto (
    val username: String? = null,
    val password: String? = null
)