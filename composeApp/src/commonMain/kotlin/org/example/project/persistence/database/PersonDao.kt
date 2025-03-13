package org.example.project.persistence.database

interface PersonDao {

    fun insertPerson(
        personId: Long,
        name: String?,
    )
}