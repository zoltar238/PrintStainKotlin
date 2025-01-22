package org.example.project.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.example.project.persistence.preferences.PreferencesManager

@Composable
fun SettingsView() {
    // Get coroutine scope
    val scope = rememberCoroutineScope()

    Column {
        // Button for deleting all preferences
        Button(onClick = {
            scope.launch {
                PreferencesManager.deleteAllPreferences()
            }
        }) {
            Text("Delete all saved data")
        }
    }
}
