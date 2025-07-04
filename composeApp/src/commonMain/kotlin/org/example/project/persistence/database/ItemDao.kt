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
        fileStructure: String?,
        timesUploaded: Long?,
        personId: Long?,
    )

    suspend fun uploadFileStructure(itemId: Long, fileStructure: String)
    fun getAllItemsWithRelation(): Flow<List<ItemWithRelations>>
    fun getItemWithRelationsById(itemId: Long): Flow<ItemWithRelations?>
    suspend fun deleteItem(itemId: Long)
}