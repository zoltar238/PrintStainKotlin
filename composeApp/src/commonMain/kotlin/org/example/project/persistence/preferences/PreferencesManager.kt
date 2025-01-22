package org.example.project.persistence.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

object PreferencesManager : PreferencesManagerContract {

    // TODO: Add deletion of user credentials
    private lateinit var pref: DataStore<Preferences>

    // Initialize dataStore
    fun initPreferences(dataStoreInit: DataStore<Preferences>) {
        pref = dataStoreInit
    }

    // #######################################################
    // Save functionality
    // #######################################################

    // Generic method for saving data
    private suspend fun <T> saveData(key: Preferences.Key<T>, data: T) {
        pref.edit { preferences ->
            preferences[key] = data
        }
    }

    // Save username
    suspend fun saveUsername(username: String) = saveData(AuthPreferenceKeys.USERNAME_KEY, username)

    // Save password
    suspend fun savePassword(password: String) = saveData(AuthPreferenceKeys.PASSWORD_KEY, password)

    // Save token
    suspend fun saveToken(token: String) = saveData(AuthPreferenceKeys.TOKEN_KEY, token)


    // #######################################################
    // Read functionality
    // #######################################################

    // Generic method for reading data
    private suspend fun <T> getPreference(key: Preferences.Key<T>): T? {
        val preferences = pref.data.first()
        return preferences[key]
    }

    // Read username
    suspend fun getUsername(): String? = getPreference(AuthPreferenceKeys.USERNAME_KEY)

    // Read password
    suspend fun getPassword(): String? = getPreference(AuthPreferenceKeys.PASSWORD_KEY)

    // Read token
    suspend fun getToken(): String? = getPreference(AuthPreferenceKeys.TOKEN_KEY)


    // #######################################################
    // Delete functionality
    // #######################################################

    // Delete token
    suspend fun clearToken() {
        pref.edit { preferences ->
            preferences.remove(AuthPreferenceKeys.TOKEN_KEY)
        }
    }

    // Delete all preferences
    override suspend fun deleteAllPreferences() {
        pref.edit { preferences ->
            preferences.clear()
        }
    }


    // #######################################################
    // App functionality
    // #######################################################
    override suspend fun saveUser(username: String, password: String, token: String) {
        saveUsername(username)
        savePassword(password)
        saveToken(token)
    }

}