package org.example.project.persistence.database

import app.cash.sqldelight.db.SqlDriver
import org.example.project.PrintStainDatabase


val DB_NAME = "printStainDatabase.db"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DriverFactory() {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): PrintStainDatabase {
    return PrintStainDatabase(driverFactory.createDriver())
}