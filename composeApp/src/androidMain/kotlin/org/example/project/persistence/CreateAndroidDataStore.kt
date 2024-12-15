package org.example.project.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.example.project.persistence.preferences.DATA_STORE_FILE_NAME


fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return org.example.project.persistence.preferences.createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}