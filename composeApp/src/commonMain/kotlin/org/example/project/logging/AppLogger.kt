package org.example.project.logging

expect object AppLogger {
    fun e(tag: String, message: String, throwable: Throwable?)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
}