package org.example.project.persistence.repository

interface ItemsRepo {
    // function reserved for admin privileges
    fun getAllItems(): String

    fun getAllUserItems(): String
}