package org.example.project.persistence.repository

import org.example.project.model.UserDto

interface UserRepo {

    fun registerUser(userDto: UserDto): String

    fun loginUser(userDto: UserDto): String
}