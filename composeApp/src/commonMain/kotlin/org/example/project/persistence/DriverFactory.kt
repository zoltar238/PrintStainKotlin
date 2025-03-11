package org.example.project.persistence

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {

    suspend fun createDriver(): SqlDriver
}