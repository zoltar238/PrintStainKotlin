package org.example.project.logging

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppLogger {
    fun e(message: String, throwable: Throwable?)
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
}