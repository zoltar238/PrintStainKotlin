package org.example.project.persistence.repository

import org.example.project.model.ItemDto
import org.example.project.persistence.network.ResponseApi

interface ItemsRepo {
    // function reserved for admin privileges
    fun getAllItems(): ResponseApi<List<ItemDto>>

    fun getAllUserItems(): ResponseApi<List<ItemDto>>
}