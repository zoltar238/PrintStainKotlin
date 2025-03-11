package org.example.project.persistence

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.project.PrintStainDatabase

class DbHelper(val driverFactory: DriverFactory) {

    private var db: PrintStainDatabase? = null

    private val mutex = Mutex()

    suspend fun <Result: Any> withDatabase(block: suspend(PrintStainDatabase) -> Result): Result = mutex.withLock {
        if (db == null) {
            db = createDb(driverFactory)
        }

        return@withLock block(db!!)
    }

    private suspend fun createDb(driverFactory: DriverFactory): PrintStainDatabase {
        return PrintStainDatabase(driver = driverFactory.createDriver())
    }
}