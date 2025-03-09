package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.example.project.model.entity.Image
import org.example.project.model.entity.Item
import org.example.project.model.entity.Person
import org.example.project.persistence.createAndroidDataStore
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.persistence.repository.initRealm
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