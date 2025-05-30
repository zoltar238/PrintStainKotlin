package org.example.project.logging

import android.util.Log
import java.time.Instant

actual object AppLogger {
    actual fun e(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(Instant.now(), message, throwable)
        } else {
            Log.e(Instant.now(), message)
        }
    }

    actual fun d(message: String) {
        Log.d(Instant.now(), message)
    }

    actual fun i(message: String) {
        Log.i(Instant.now(), message)
    }

    actual fun w(message: String) {
        Log.w(Instant.now(), message)
    }
}