package org.example.project.persistence.repository

import org.example.project.logging.LoggingTags
import org.example.project.model.UserDto
import org.example.project.persistence.network.ClientApi
import org.example.project.persistence.network.ResponseApi

object UserRepoHttpImp : UserRepo {

    override fun registerUser(userDto: UserDto): ResponseApi<String>? {
        return responseHandler(
            LoggingTags.UserRegistration.name,
            "String"
        ) { ClientApi.userApi.registerUser(userDto) }
    }

    override fun loginUser(userDto: UserDto): ResponseApi<String>? {
        return responseHandler(
            LoggingTags.Userlogin.name,
            "String"
        ) { ClientApi.userApi.loginUser(userDto) }
    }
}
