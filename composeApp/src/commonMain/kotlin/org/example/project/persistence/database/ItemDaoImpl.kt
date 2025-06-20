package org.example.project.persistence.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import comexampleproject.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.PrintStainDatabase
import org.example.project.model.dto.ItemWithRelations


class ItemDaoImpl(db: PrintStainDatabase) : ItemDao {

    private val query = db.itemEntityQueries

    override fun getItemById(id: Long): Item? =
        query.selectItemById(id).executeAsOneOrNull()

    override suspend fun insertItem(
        itemId: Long,
        name: String?,
        description: String?,
        postDate: String?,
        fileStructure: String?,
        timesUploaded: Long?,
        personId: Long?,
    ) {
        query.insertItem(
            itemId = itemId,
            name = name,
            description = description,
            postDate = postDate,
            fileStructure = fileStructure,
            timesUploaded = timesUploaded,
            personId = personId
        )
    }

    override suspend fun uploadFileStructure(itemId: Long, fileStructure: String) {
        query.updateFileStructure(
            fileStructure = fileStructure,
            itemId = itemId
        )
    }

    private fun List<SelectAllItemWithRelations>.processToItemsWithRelations(): List<ItemWithRelations> {
        return this
            .groupBy { it.itemId } // Agrupar por ID de ítem
            .map { (itemId, rows) ->
                val firstRow = rows.first()

                // Map Item
                val item = Item(
                    itemId = itemId,
                    name = firstRow.name,
                    description = firstRow.description,
                    postDate = firstRow.postDate,
                    timesUploaded = firstRow.timesUploaded,
                    personId = firstRow.personId,
                    archived = firstRow.archived,
                    fileStructure = firstRow.fileStructure,
                )

                // Map person
                val person = firstRow.personId?.let {
                    Person(
                        personId = it,
                        name = firstRow.personName ?: "",
                        isActive = firstRow.personIsActive ?: true,
                        username = firstRow.personUsername ?: "",
                    )
                }

                // Map images
                val images = rows.mapNotNull { row ->
                    row.imageId?.let { imageId ->
                        Image(
                            imageId = imageId,
                            base64Image = row.imageData ?: "",
                            item_id = row.imageId
                        )
                    }
                }.toCollection(LinkedHashSet()).toList()

                // Map sales
                val sales = rows.mapNotNull { row ->
                    row.sale_saleId?.let { saleId ->
                        Sale(
                            saleId = saleId,
                            date = row.sale_date,
                            cost = row.sale_cost,
                            price = row.sale_price,
                            itemId = row.imageId,
                            status = row.sale_status,
                        )
                    }
                }

                ItemWithRelations(item, person, images, sales)
            }
    }

    override fun getAllItemsWithRelation(): Flow<List<ItemWithRelations>> {
        return query.selectAllItemWithRelations()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.processToItemsWithRelations()
            }
    }

    override fun getItemWithRelationsById(itemId: Long): Flow<ItemWithRelations?> {
    return query.selectItemWithRelationsById(itemId)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows ->
            if (rows.isEmpty()) {
                null
            } else {
                val firstRow = rows.first()

                // Map Item (solo una vez)
                val item = Item(
                    itemId = firstRow.itemId,
                    name = firstRow.name,
                    description = firstRow.description,
                    postDate = firstRow.postDate,
                    timesUploaded = firstRow.timesUploaded,
                    personId = firstRow.personId,
                    archived = firstRow.archived,
                    fileStructure = firstRow.fileStructure
                )

                // Map Person (solo una vez)
                val person = if (firstRow.personPersonId != null) {
                    Person(
                        personId = firstRow.personPersonId,
                        name = firstRow.personName ?: "",
                        isActive = firstRow.personIsActive ?: true,
                        username = firstRow.personUsername ?: ""
                    )
                } else null

                // Map ALL Images (de todas las filas)
                val images = rows.mapNotNull { row ->
                    row.imageId?.let {
                        Image(
                            imageId = it,
                            base64Image = row.imageData ?: "",
                            item_id = row.itemId
                        )
                    }
                }.distinctBy { it.imageId }

                // Map ALL Sales (de todas las filas)
                val sales = rows.mapNotNull { row ->
                    row.sale_saleId?.let {
                        Sale(
                            saleId = it,
                            date = row.sale_date,
                            cost = row.sale_cost,
                            price = row.sale_price,
                            itemId = row.itemId,
                            status = row.sale_status
                        )
                    }
                }.distinctBy { it.saleId }

                ItemWithRelations(
                    item = item,
                    person = person,
                    images = images,
                    sales = sales
                )
            }
        }
}


    override suspend fun deleteItem(itemId: Long) {
        query.deleteItem(itemId)
    }
}