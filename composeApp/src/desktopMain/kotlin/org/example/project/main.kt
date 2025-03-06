package org.example.project

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.example.project.model.entity.Image
import org.example.project.model.entity.Item
import org.example.project.model.entity.Person
import org.example.project.persistence.preferences.DATA_STORE_FILE_NAME
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.preferences.createDataStore
import org.example.project.ui.navigation.AppNavigation



fun main() = application {

    // Initialize preferences datastore
    PreferencesManager.initPreferences(
        createDataStore { DATA_STORE_FILE_NAME }
    )

    // Set windows state and launch app centered
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
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