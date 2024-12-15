package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.persistence.createAndroidDataStore
import org.example.project.persistence.preferences.DATA_STORE_FILE_NAME
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.preferences.createDataStore
import org.example.project.ui.auth.AuthScreen
import org.example.project.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize preferences datastore
            PreferencesManager.initPreferences(
                createAndroidDataStore(applicationContext)
            )

            // Launch application
            AppNavigation()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
}