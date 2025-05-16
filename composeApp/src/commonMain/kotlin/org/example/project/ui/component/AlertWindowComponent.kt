package org.example.project.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmButton: String = "Accept",
    onConfirm: () -> Unit,
    dismissButton: String = "Cancel",
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(confirmButton)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(dismissButton)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}