package org.example.project.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.sql.Timestamp

@Serializable
data class AllSalesDto(
    @Contextual val cost: BigDecimal? = null,
    @Contextual val date: Timestamp? = null,
    val itemName: String? = null,
    @Contextual val price: BigDecimal? = null,
    val saleId: Long? = null,
)
