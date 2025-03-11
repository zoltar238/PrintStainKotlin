package org.example.project.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import comexampleproject.Image
import comexampleproject.Item
import comexampleproject.Person
import comexampleproject.SelectAllItemWithRelations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.project.PrintStainDatabase
import org.example.project.model.dto.ItemWithRelations
import org.example.project.persistence.repository.ItemDao


class ItemDaoImpl(db: PrintStainDatabase) : ItemDao {

    private val queries = db.itemEntityQueries

    fun mapItem(
        itemId: Long,
        name: String?,
        description: String?,
        postDate: String?,
        startDate: String?,
        finishDate: String?,
        shipDate: String?,
        timesUploaded: Long?,
        person_id: Long?,
    ): Item {
        return Item(
            itemId = itemId,
            name = name,
            description = description,
            postDate = postDate,
            startDate = startDate,
            finishDate = finishDate,
            shipDate = shipDate,
            timesUploaded = timesUploaded,
            person_id = person_id
        )
    }

    override suspend fun getItemById(id: Long): Item? {
        return withContext(Dispatchers.IO) {
            queries.selectItemById(id).executeAsOneOrNull()
        }
    }

    override suspend fun insertItem(
        itemId: Long,
        name: String?,
        description: String?,
        postDate: String?,
        startDate: String?,
        finishDate: String?,
        shipDate: String?,
        timesUploaded: Long?,
        person_id: Long?,
    ) {
        queries.insertItem(
            itemId = itemId,
            name = name,
            description = description,
            postDate = postDate,
            startDate = startDate,
            finishDate = finishDate,
            shipDate = shipDate,
            timesUploaded = timesUploaded,
            person_id = person_id
        )
    }

    private fun List<SelectAllItemWithRelations>.processToItemsWithRelations(): List<ItemWithRelations> {
        return this
            .groupBy { it.itemId } // Agrupar por ID de ítem
            .map { (itemId, rows) ->
                val firstRow = rows.first()

                // Mapear el Item
                val item = Item(
                    itemId = itemId,
                    name = firstRow.name,
                    description = firstRow.description,
                    postDate = firstRow.postDate,
                    startDate = firstRow.startDate,
                    finishDate = firstRow.finishDate,
                    shipDate = firstRow.shipDate,
                    timesUploaded = firstRow.timesUploaded,
                    person_id = firstRow.person_id
                )

                // Mapear la Persona (relación uno a uno)
                val person = firstRow.person_personId?.let {
                    Person(
                        personId = it,
                        name = firstRow.person_name ?: ""
                    )
                }

                // Mapear las Imágenes (relación uno a muchos)
                val images = rows.mapNotNull { row ->
                    row.image_id?.let { imageId ->
                        Image(
                            imageId = imageId,
                            base64Image = row.image_data ?: "",
                            item_id = row.image_id
                        )
                    }
                }

                ItemWithRelations(item, person!!, images)
            }
    }

    override fun getAllItemsWithRelation(): Flow<List<ItemWithRelations>> {
        return queries.selectAllItemWithRelations()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.processToItemsWithRelations()
            }
    }

    override fun getAllItems(): Flow<List<Item>> {
        TODO("Not")
//        return queries.selectAllItems().asFlow().mapToList()
    }
}