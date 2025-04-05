package org.example.project.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import org.example.project.viewModel.MessageEvent

// Todo: further customize toast
@Composable
fun MessageToaster(
    messageEvent: MessageEvent?,
    success: Boolean,
    onMessageConsumed: () -> Unit,
) {
    val toaster = rememberToasterState()

    LaunchedEffect(messageEvent) {
        messageEvent?.let { event ->
            if (!event.isConsumed) {
                toaster.show(
                    message = event.message,
                    type = if (success) ToastType.Success else ToastType.Error
                )
                onMessageConsumed() // Consumimos el mensaje después de mostrarlo
            }
        }
    }

    Toaster(
        state = toaster,
        showCloseButton = true,
        alignment = Alignment.TopCenter
    )
}
