package org.example.project.service.mapping

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import org.example.project.model.dto.ImageDto
import org.example.project.model.dto.ItemDto
import org.example.project.model.dto.PersonDto
import org.example.project.model.entity.Image
import org.example.project.model.entity.Item
import org.example.project.model.entity.Person
import java.time.OffsetDateTime

object Mapper {

    fun mapImageDtoToRealm(imageDto: ImageDto): Image {
        return Image().apply {
            id = imageDto.id
            base64Image = imageDto.base64Image
        }
    }

    fun mapPersonDtoToRealm(personDto: PersonDto): Person {
        return Person().apply {
            id = personDto.id
            username = personDto.username
        }
    }

    fun mapItemDtoToRealm(itemDto: ItemDto): Item {
        return Item().apply {
            id = itemDto.itemId
            name = itemDto.name
            description = itemDto.description
            postDate = itemDto.postDate.toRealmInstant()
            startDate = itemDto.startDate.toRealmInstant()
            finishDate = itemDto.finishDate.toRealmInstant()
            shipDate = itemDto.shipDate.toRealmInstant()
            timesUploaded = itemDto.timesUploaded
            images = itemDto.images.toRealmList(::mapImageDtoToRealm)
            person = itemDto.person?.let { mapPersonDtoToRealm(it) }
        }
    }

    private fun OffsetDateTime?.toRealmInstant(): RealmInstant? {
        this ?: return null

        // // Convert OffsetDateTime to milliseconds since epoch
        val epochMillis = this.toInstant().toEpochMilli()

        // // Create a RealmInstant using the milliseconds
        return RealmInstant.from(epochMillis / 1000, (epochMillis % 1000).toInt() * 1_000_000)
    }

    // Map List to RealmList
    private fun <T, R> List<T>?.toRealmList(mapper: (T) -> R): RealmList<R>? {
        this ?: return null

        val realmList: RealmList<R> = realmListOf()

        forEach { item ->
            realmList.add(mapper(item))
        }

        return realmList
    }
}
