package org.example.project.persistence

data class QueryResult<T>(
    val data: T,
    val queryResultInfo: QueryResultInfo
)