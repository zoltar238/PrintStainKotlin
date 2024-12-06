package org.example.project.entity

import kotlinx.serialization.Serializable

@Serializable
data class ServerDto<T>(
    val success: Boolean,
    val response: String,
    val data: T
)
