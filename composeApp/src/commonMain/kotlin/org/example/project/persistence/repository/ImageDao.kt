package org.example.project.persistence.repository

interface ImageDao {
    suspend fun insertImage(
        imageId: Long,
        base64Image: String,
        item_id: Long,
    )
}