package org.example.project.logging

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppLogger {
    fun e(tag: String, message: String, throwable: Throwable?)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
}