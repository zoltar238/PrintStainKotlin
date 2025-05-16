package org.example.project.persistence.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.PrintStainDatabase
import org.example.project.initializer.AppContextWrapper

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory actual constructor() {
    actual suspend fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(PrintStainDatabase.Schema, AppContextWrapper.appContext!!, DB_NAME)
    }
}