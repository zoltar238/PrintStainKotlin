package org.example.project.persistence.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.project.PrintStainDatabase
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver {
        val dbFilePath: String = getPath(isDebug = false)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFilePath}")

        if (!File(dbFilePath).exists()) {
            PrintStainDatabase.Schema.create(driver)
        }

        return driver
    }

    private fun getPath(isDebug: Boolean): String {
        val propertyKey = if (isDebug) "java.io.tmpdir" else "user.home"
        val parentFolderPath = System.getProperty(propertyKey) + "/PrintStainDatabase"
        val parentFolder = File(parentFolderPath)
        if (!parentFolder.exists()) {
            parentFolder.mkdirs()
        }

        val databasePath = File(parentFolderPath, DB_NAME)
        return databasePath.absolutePath
    }
}