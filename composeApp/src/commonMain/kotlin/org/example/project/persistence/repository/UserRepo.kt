package org.example.project.persistence.repository

import org.example.project.model.UserDto
import org.example.project.persistence.network.ResponseApi

interface UserRepo {

    fun registerUser(userDto: UserDto): String

    fun loginUser(userDto: UserDto): ResponseApi<String>
}