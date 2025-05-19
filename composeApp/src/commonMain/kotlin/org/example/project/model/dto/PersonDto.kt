package org.example.project.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val personId: Long? = null,
    val isActive: Boolean? = true,
    val name: String? = null,
    val surname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val roles: List<String>? = null,
)
