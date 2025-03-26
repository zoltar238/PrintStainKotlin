package org.example.project.persistence.database

import comexampleproject.Item
import kotlinx.coroutines.flow.Flow
import org.example.project.model.dto.ItemWithRelations

interface ItemDao {

    fun getItemById(id: Long): Item?
    suspend fun insertItem(
        itemId: Long,
        name: String?,
        description: String?,
        postDate: String?,
        timesUploaded: Long?,
        personId: Long?,
    )

    fun getAllItemsWithRelation(): Flow<List<ItemWithRelations>>
    fun getAllItems(): Flow<List<Item>>
    suspend fun deleteItem(itemId: Long)
}