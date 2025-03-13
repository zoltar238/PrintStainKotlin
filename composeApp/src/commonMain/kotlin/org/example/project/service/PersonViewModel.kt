package org.example.project.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.PrintStainDatabase
import org.example.project.persistence.database.DriverFactory
import org.example.project.persistence.database.PersonDao
import org.example.project.persistence.database.PersonDaoImpl

class PersonViewModel : ViewModel() {

    private val db: DriverFactory = DriverFactory()
    private val database = PrintStainDatabase.invoke(driver = db.createDriver())
    private val personDao: PersonDao = PersonDaoImpl(database)

    fun insertPerson(
        personId: Long,
        name: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            personDao.insertPerson(
                personId = personId,
                name = name
            )
        }
    }
}