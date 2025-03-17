package org.example.project.persistence.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import comexampleproject.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.example.project.PrintStainDatabase

class PersonDaoImpl(db: PrintStainDatabase) : PersonDao {

    val query = db.personEntityQueries

    override suspend fun insertPerson(personId: Long, name: String?) {
        query.insertOrReplacePerson(
            personId = personId,
            name = name
        )
    }

    override fun getAllPersons(): Flow<List<Person>> {
        return query.selectAllPersons().asFlow().mapToList(Dispatchers.IO)
    }
}