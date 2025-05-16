package org.example.project.persistence.database

import comexampleproject.Person
import kotlinx.coroutines.flow.Flow

interface PersonDao {

    suspend fun insertPerson(
        personId: Long,
        name: String?,
    )

    fun getAllPersons(): Flow<List<Person>>
}