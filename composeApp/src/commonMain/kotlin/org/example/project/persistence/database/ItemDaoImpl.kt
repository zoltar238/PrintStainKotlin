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
        timesUploaded: Long?,
        personId: Long?,
    ) {
        query.insertItem(
            itemId = itemId,
            name = name,
            description = description,
            postDate = postDate,
            timesUploaded = timesUploaded,
            personId = personId
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
                    archived = firstRow.archived
                )

                // Map person
                val person = firstRow.person_personId?.let {
                    Person(
                        personId = it,
                        name = firstRow.person_name ?: "",
                    )
                }

                // Map images
                val images = rows.mapNotNull { row ->
                    row.image_id?.let { imageId ->
                        Image(
                            imageId = imageId,
                            base64Image = row.image_data ?: "",
                            item_id = row.image_id
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
                            itemId = row.image_id,
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

    override fun getAllItems(): Flow<List<Item>> {
        return query.selectAllItems().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun deleteItem(itemId: Long) {
        query.deleteItem(itemId)
    }
}