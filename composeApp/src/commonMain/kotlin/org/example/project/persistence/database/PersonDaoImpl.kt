package org.example.project.persistence.database

import org.example.project.PrintStainDatabase

class PersonDaoImpl(db: PrintStainDatabase) : PersonDao {

    val query = db.personEntityQueries

    override suspend fun insertPerson(personId: Long, name: String?) {
        query.insertOrReplacePerson(
            personId = personId,
            name = name
        )
    }
}