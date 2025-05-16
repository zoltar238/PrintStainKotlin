package org.example.project.controller

import org.example.project.model.dto.LoginDto
import org.example.project.model.dto.PersonDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserController {
    @POST("person/register")
    suspend fun registerUser(@Body user: PersonDto): Response<ResponseApi<String>>

    @POST("person/login")
    suspend fun loginUser(@Body user: LoginDto): Response<ResponseApi<String>>
}