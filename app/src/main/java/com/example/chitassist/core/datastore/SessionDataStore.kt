package com.example.chitassist.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val LOGGED_IN = booleanPreferencesKey("logged_in")
        private val USER_ID = longPreferencesKey("user_id")
        private val THEME = stringPreferencesKey("theme")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LOGGED_IN] ?: false
    }

    val userId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    suspend fun saveSession(userId: Long) {
        dataStore.edit { preferences ->
            preferences[LOGGED_IN] = true
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences[LOGGED_IN] = false
            preferences[USER_ID] = -1L
        }
    }

    suspend fun updateTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
