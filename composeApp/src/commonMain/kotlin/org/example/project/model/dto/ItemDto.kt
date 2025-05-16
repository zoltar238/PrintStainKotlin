package org.example.project.model.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.example.project.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class ItemDto(
    val itemId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val fileStructure: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val postDate: OffsetDateTime? = null,
    val timesUploaded: Long? = null,
    @Contextual val images: List<ImageDto>? = emptyList(),
    val hashtags: List<String>? = emptyList(),
    @Contextual val person: PersonDto? = null,
)
