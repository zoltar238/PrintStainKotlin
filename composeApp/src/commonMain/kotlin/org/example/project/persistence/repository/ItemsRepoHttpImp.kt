package org.example.project.persistence.repository

import kotlinx.coroutines.runBlocking
import org.example.project.model.ItemDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.preferences.PreferencesManager


// Todo: improve error handling when failed authorization and
object ItemsRepoHttpImp : ItemsRepo {
    override fun getAllItems(): ResponseApi<List<ItemDto>> {

        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return if (token != null) {
            runBlocking {
                ClientApi.itemsApi.getAllItems("Bearer $token")
            }
        } else {
            return ResponseApi(false, "Error connecting to server", emptyList())
        }
    }

    override fun getAllUserItems(): ResponseApi<List<ItemDto>> {
        // Obtain access token
        val token = runBlocking {
            PreferencesManager.getToken()
        }

        return if (token != null) {
            runBlocking {
                ClientApi.itemsApi.getAllUserItems("Bearer $token")
            }
        } else {
            return ResponseApi(false, "Error connecting to server", emptyList())
        }
    }
}