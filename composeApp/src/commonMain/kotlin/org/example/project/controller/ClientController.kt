package org.example.project.controller

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ClientController {
    private const val BASE_URL = "http://localhost:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
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
