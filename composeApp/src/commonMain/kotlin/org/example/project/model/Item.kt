package org.example.project.model

import androidx.datastore.preferences.protobuf.Timestamp
import java.math.BigDecimal

data class Item(
    val item_id: Long,
    val name: String,
    val description: String,
    val cost: BigDecimal,
    val times_uploaded: Int,
    val finish_date: Timestamp,
    val post_date: Timestamp,
    val ship_date: Timestamp,
    val owner: String,
    val link: String,
    val start_date: Timestamp
)
