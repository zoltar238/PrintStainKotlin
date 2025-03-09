package org.example.project.persistence.repository

import org.example.project.model.dto.PersonDto
import org.example.project.controller.ResponseApi

interface UserRepo {
    fun registerUser(personDto: PersonDto): ResponseApi<String>?

    fun loginUser(personDto: PersonDto): ResponseApi<String>?
}
