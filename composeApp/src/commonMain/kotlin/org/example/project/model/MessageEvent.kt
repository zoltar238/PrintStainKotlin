package org.example.project.model

data class MessageEvent(
    val message: String,
    val isConsumed: Boolean = false,
) {
    fun consume(): MessageEvent = copy(isConsumed = true)
}