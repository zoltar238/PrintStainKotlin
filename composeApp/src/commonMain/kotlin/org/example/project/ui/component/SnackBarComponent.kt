package org.example.project.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun SnackBarComponent(snackbarHostState: SnackbarHostState, snackBarColor: MutableState<Color>) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(top = 16.dp)
                .zIndex(3f),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = snackBarColor.value,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 5.dp
                )
            }
        )
    }
}