package org.example.project.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.datastore.preferences.protobuf.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val itemId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    @Contextual val postDate: Timestamp? = null,
    @Contextual val startDate: Timestamp? = null,
    @Contextual val finishDate: Timestamp? = null,
    @Contextual val shipDate: Timestamp? = null,
    val timesUploaded: Int? = null,
    val base64Images: List<String>? = emptyList(),
    val bitmapImages: MutableList<ImageBitmap> = mutableListOf(),
    val hashtags: List<String>? = emptyList(),
    val poster: String? = null,
)
