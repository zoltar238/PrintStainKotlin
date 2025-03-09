package org.example.project.model.dto

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.example.project.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class ItemDto(
    val itemId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val postDate: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val startDate: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val finishDate: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val shipDate: OffsetDateTime? = null,
    val timesUploaded: Int? = null,
    val base64Images: List<String>? = emptyList(),
    @Contextual val bitmapImages: MutableList<ImageBitmap> = mutableListOf(),
    val hashtags: List<String>? = emptyList(),
    @Contextual val person: PersonDto? = null,
)
