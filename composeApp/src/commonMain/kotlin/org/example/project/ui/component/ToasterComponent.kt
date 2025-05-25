package org.example.project.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import org.example.project.model.MessageEvent


@Composable
fun MessageToaster(
    messageEvents: List<MessageEvent?>,
    success: Boolean,
    onMessageConsumed: List<Unit>,
) {
    val toaster = rememberToasterState()

    LaunchedEffect(messageEvents) {
        messageEvents.forEachIndexed { index, event ->
            event?.let {
                if (!it.isConsumed) {
                    event.message?.let {
                        toaster.show(
                            message = it,
                            type = if (success) ToastType.Success else ToastType.Error
                        )
                    }
                    // Consume the message after showing it
                    onMessageConsumed[index]
                }
            }
        }
    }

    Toaster(
        state = toaster,
        showCloseButton = true,
        alignment = Alignment.TopCenter,
        richColors = true
    )
}
