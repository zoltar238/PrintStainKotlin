package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.persistence.database.createAndroidDataStore
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize preferences datastore
            PreferencesDaoImpl.initPreferences(
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