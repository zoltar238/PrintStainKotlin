package org.example.project.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val id: Long? = null,
    val base64Image: String? = null,
)