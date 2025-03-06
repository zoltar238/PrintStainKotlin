package org.example.project.persistence.repository

import org.example.project.model.UserDto
import org.example.project.controller.ResponseApi

interface UserRepo {
    fun registerUser(userDto: UserDto): ResponseApi<String>?

    fun loginUser(userDto: UserDto): ResponseApi<String>?
}
