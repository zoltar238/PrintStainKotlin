package org.example.project.persistence.database

interface PersonDao {

    suspend fun insertPerson(
        personId: Long,
        name: String?,
    )
}