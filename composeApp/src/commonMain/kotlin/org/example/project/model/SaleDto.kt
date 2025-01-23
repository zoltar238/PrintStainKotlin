package org.example.project.model

import kotlinx.serialization.Contextual
import java.math.BigDecimal
import java.sql.Timestamp

data class SaleDto(
    @Contextual val date: Timestamp? = null,
    @Contextual val cost: BigDecimal? = null,
    @Contextual val price: BigDecimal? = null,
    val itemId: Long? = null,
)
