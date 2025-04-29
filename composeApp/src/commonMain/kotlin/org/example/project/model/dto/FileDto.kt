package org.example.project.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileDto (
    val fileId: Long? = null,
    var fileName: String? = null,
    val fileSize: Long? = null,
    val fileExtension: String? = null,
    val fileContent: ByteArray? = null,
    val fileType: String? = null,
)
