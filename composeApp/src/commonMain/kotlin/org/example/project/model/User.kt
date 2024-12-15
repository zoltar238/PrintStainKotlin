package org.example.project.model

data class User(
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val username: String,
    val password: String,
    val roles: List<String>? = null,
)
