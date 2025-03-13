package org.example.project.persistence.database

import org.example.project.PrintStainDatabase

class ImageDaoImpl(db: PrintStainDatabase) : ImageDao {

    private val query = db.imageEntityQueries

    override suspend fun insertImage(
        imageId: Long,
        base64Image: String,
        item_id: Long,
    ) {
        query.insertImage(
            imageId = imageId,
            base64Image = base64Image,
            item_id = item_id
        )
    }
}