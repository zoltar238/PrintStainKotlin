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
    messageEventList: List<MessageEvent?>,
    successList: List<Boolean>,
    onMessageConsumedList: List<Unit>,
) {
    val toaster = rememberToasterState()

    LaunchedEffect(messageEventList) {
        messageEventList.forEachIndexed { index, event ->
            event?.let {
                if (!it.isConsumed) {
                    event.message?.let {
                        toaster.show(
                            message = it,
                            type = if (successList[index]) ToastType.Success else ToastType.Error
                        )
                    }
                    // Consume the message after showing it
                    onMessageConsumedList[index]
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
