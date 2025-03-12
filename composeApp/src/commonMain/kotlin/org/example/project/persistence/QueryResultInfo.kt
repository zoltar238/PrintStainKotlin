package org.example.project.persistence

data class QueryResultInfo(
    val success: Boolean,
    val errorMessage: String?,
    val errorType: ErrorType?
)