package org.example.project

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.persistence.preferences.DATA_STORE_FILE_NAME
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.preferences.createDataStore
import org.example.project.persistence.repository.ItemsRepoHttpImp
import org.example.project.service.getAllItems
import org.example.project.ui.navigation.AppNavigation

fun main() = application {

    // Initialize preferences datastore
    PreferencesManager.initPreferences(
        createDataStore { DATA_STORE_FILE_NAME }
    )

    val windowState = rememberWindowState(
        width = 800.dp,
        height = 800.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "PrintStain",
        state = windowState
    ) {
        // Launch application
        AppNavigation()
    }
}