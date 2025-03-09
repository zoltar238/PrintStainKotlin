package org.example.project.persistence.repository

import org.example.project.model.dto.ItemDto
import org.example.project.controller.ResponseApi

interface ItemsRepo {
    // function reserved for admin privileges
    fun getAllItems(): ResponseApi<List<ItemDto>>?

    fun getAllUserItems(): ResponseApi<List<ItemDto>>?
}