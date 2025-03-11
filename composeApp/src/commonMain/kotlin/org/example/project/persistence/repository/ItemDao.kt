package org.example.project.persistence.repository

import comexampleproject.Item
import kotlinx.coroutines.flow.Flow
import org.example.project.model.dto.ItemWithRelations

interface ItemDao {

    suspend fun getItemById(id: Long): Item?

    suspend fun insertItem(
        itemId: Long,
        name: String?,
        description: String?,
        postDate: String?,
        startDate: String?,
        finishDate: String?,
        shipDate: String?,
        timesUploaded: Long?,
        person_id: Long?,
    )

    fun getAllItemsWithRelation(): Flow<List<ItemWithRelations>>

    fun getAllItems(): Flow<List<Item>>
}