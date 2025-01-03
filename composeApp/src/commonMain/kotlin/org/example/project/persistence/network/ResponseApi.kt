package org.example.project.persistence.network

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseApi<T>(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("response") val response: String,
    @JsonProperty("data") val data: T
)
