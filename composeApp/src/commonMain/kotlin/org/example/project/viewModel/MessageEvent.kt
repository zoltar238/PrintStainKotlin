package org.example.project.viewModel

data class MessageEvent(
    val message: String,
    val isConsumed: Boolean = false,
) {
    fun consume(): MessageEvent = copy(isConsumed = true)
}