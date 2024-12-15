package org.example.project.persistence.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import androidx.datastore.preferences.core.Preferences

fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {producePath().toPath()}
    )
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"