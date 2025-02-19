package org.example.project.persistence.network

import org.example.project.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("person/register")
    suspend fun registerUser(@Body user: UserDto): Response<ResponseApi<String>>

    @POST("person/login")
    suspend fun loginUser(@Body user: UserDto): Response<ResponseApi<String>>
}