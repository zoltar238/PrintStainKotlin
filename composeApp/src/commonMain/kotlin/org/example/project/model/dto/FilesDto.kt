package org.example.project.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FilesDto(
    val itemId: Long? = null,
    val fileName: String? = null,
    val file : List<FileDto>
)
