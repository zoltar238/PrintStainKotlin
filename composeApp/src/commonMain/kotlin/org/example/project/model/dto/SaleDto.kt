package org.example.project.model.dto

import kotlinx.serialization.Serializable
import org.example.project.util.BigDecimalSerializer
import org.example.project.util.OffsetDateTimeSerializer
import java.math.BigDecimal
import java.time.OffsetDateTime

@Serializable
data class SaleDto(
    val saleId: Long? = null,
    @Serializable(with = OffsetDateTimeSerializer::class) val date: OffsetDateTime? = null,
    @Serializable(with = BigDecimalSerializer::class) val cost: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal? = null,
    val status: String? = null,
    val itemId: Long? = null,
)
