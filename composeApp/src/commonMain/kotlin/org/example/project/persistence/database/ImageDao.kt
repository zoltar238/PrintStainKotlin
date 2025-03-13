package org.example.project.persistence.database

interface ImageDao {
    suspend fun insertImage(
        imageId: Long,
        base64Image: String,
        item_id: Long,
    )
}