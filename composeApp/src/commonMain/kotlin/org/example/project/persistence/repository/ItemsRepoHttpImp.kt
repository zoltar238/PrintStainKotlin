package org.example.project.persistence.repository

import kotlinx.coroutines.runBlocking
import org.example.project.logging.LoggingTags
import org.example.project.model.ItemDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.preferences.PreferencesManager


object ItemsRepoHttpImp : ItemsRepo {
    override fun getAllItems(): ResponseApi<List<ItemDto>>? {

        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return responseHandler(
            LoggingTags.ItemsGetAll.name,
            "List"
        ) { ClientApi.itemsApi.getAllItems("Bearer $token") }

    }

    override fun getAllUserItems(): ResponseApi<List<ItemDto>>? {
        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return responseHandler(
            LoggingTags.ItemsGetAll.name,
            "List"
        ) { ClientApi.itemsApi.getAllUserItems("Bearer $token") }

    }
}