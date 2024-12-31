package org.example.project.model

import java.math.BigDecimal
import java.sql.Timestamp

data class SaleDto(
    val date: Timestamp,
    val cost: BigDecimal,
    val price: BigDecimal,
    val itemId: Long
)
