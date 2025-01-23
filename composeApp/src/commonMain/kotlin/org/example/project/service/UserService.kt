package org.example.project.service

import org.example.project.model.UserDto
import org.example.project.persistence.network.ResponseApi
import org.example.project.persistence.repository.UserRepoHttpImp

fun registerUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server
    val serverResponse = UserRepoHttpImp.registerUser(userDto)
    return serverResponse!!
}

fun loginUser(userDto: UserDto): ResponseApi<String> {
    // Receive response from server
    val serverResponse: ResponseApi<String>? = UserRepoHttpImp.loginUser(userDto)
    return serverResponse!!
}


