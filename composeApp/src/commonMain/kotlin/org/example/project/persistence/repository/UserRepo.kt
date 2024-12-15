package org.example.project.persistence.repository

import org.example.project.model.User

interface UserRepo {

    fun registerUser(user: User): String

    fun loginUser(user: User): String
}