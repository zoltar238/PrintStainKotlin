package org.example.project.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.example.project.ui.AppColors

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
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.textOnBackgroundColor
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textOnBackgroundSecondaryColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = confirmButton,
                        color = AppColors.primaryColor
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = dismissButton,
                        color = AppColors.textOnBackgroundSecondaryColor
                    )
                }
            },
            containerColor = AppColors.surfaceColor,
            titleContentColor = AppColors.textOnBackgroundColor,
            textContentColor = AppColors.textOnBackgroundSecondaryColor
        )
    }
}