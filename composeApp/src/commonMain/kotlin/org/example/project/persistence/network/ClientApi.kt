package org.example.project.persistence.network

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ClientApi {
    private const val BASE_URL = "http://localhost:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

    }

    val saleApi: SaleApi by lazy {
        retrofit.create(SaleApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val itemsApi: ItemsApi by lazy {
        retrofit.create(ItemsApi::class.java)
    }

}
