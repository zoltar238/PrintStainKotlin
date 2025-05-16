package org.example.project.controller

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ClientController {
    private const val BASE_URL = "http://localhost:8080/"

    // JSON serialization configuration for Kotlinx
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val contentType = "application/json".toMediaType()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val saleController: SaleController by lazy {
        retrofit.create(SaleController::class.java)
    }

    val userController: UserController by lazy {
        retrofit.create(UserController::class.java)
    }

    val itemController: ItemController by lazy {
        retrofit.create(ItemController::class.java)
    }
}
