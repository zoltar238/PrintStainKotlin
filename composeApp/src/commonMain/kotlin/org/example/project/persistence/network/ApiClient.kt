package org.example.project.persistence.network

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://localhost:8080/"

    val retrofit: Retrofit by lazy {
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
}
