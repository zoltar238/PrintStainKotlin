package org.example.project.persistence.repository

import org.example.project.model.UserDto
import org.example.project.persistence.network.ApiClient
import org.example.project.persistence.network.ResponseApi

object UserRepoHttpImp : UserRepo {

    override fun registerUser(userDto: UserDto): String {
        return kotlinx.coroutines.runBlocking {
            ApiClient.userApi.registerUser(userDto)
        }
    }

    override fun loginUser(userDto: UserDto): ResponseApi<String> {
        return kotlinx.coroutines.runBlocking {
            ApiClient.userApi.loginUser(userDto)
        }
    }
}
