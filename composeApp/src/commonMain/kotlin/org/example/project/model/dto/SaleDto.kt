package org.example.project.model.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.example.project.util.OffsetDateTimeSerializer
import java.math.BigDecimal
import java.time.OffsetDateTime

@Serializable
data class SaleDto(
    @Serializable(with = OffsetDateTimeSerializer::class) val date: OffsetDateTime? = null,
    @Contextual val cost: BigDecimal? = null,
    @Contextual val price: BigDecimal? = null,
    val itemId: Long? = null,
)
