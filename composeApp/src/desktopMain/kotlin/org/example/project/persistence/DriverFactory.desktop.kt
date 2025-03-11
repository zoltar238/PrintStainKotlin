package org.example.project.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.project.PrintStainDatabase

actual class DriverFactory {

    actual suspend fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PrintStainDatabase.Schema.create(driver)
        return driver
    }

}
