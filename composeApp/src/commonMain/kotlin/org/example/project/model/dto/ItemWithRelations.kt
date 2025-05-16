package org.example.project.model.dto

import comexampleproject.Image
import comexampleproject.Item
import comexampleproject.Person
import comexampleproject.Sale


data class ItemWithRelations(
    val item: Item,
    val person: Person? = null,
    val images: List<Image>,
    val sales: List<Sale>? = null,
)
