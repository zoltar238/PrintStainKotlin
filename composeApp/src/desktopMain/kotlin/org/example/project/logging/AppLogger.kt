package org.example.project.logging

import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object AppLogger {
    private val logger: Logger = Logger.getLogger(AppLogger::class.java.name)

    init {
        logger.level = Level.FINE
    }

    actual fun e(
        message: String,
        throwable: Throwable?,
    ) {
        if (throwable != null) {
            logger.log(Level.SEVERE, "ERROR: [${Instant.now()}] $message", throwable)
        } else {
            logger.severe("ERROR: [${Instant.now()}] $message")
        }
    }

    actual fun d(message: String) {
        logger.info("DEBUG: [${Instant.now()}] $message")
    }

    actual fun i(message: String) {
        logger.info("INFO: [${Instant.now()}] $message")
    }

    actual fun w(message: String) {
        logger.warning("INFO: [${Instant.now()}] $message")
    }
}