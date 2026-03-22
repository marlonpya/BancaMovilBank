package com.microsol.bancamovil.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_TEMPLATE_KEY = stringPreferencesKey("user_template")
        private val USER_LANGUAGE_KEY = stringPreferencesKey("user_language")
        private val SESSION_TIMESTAMP_KEY = longPreferencesKey("session_timestamp")
        private val EXPIRES_IN_KEY = stringPreferencesKey("expires_in")
        private val TOKEN_TYPE_KEY = stringPreferencesKey("token_type")
    }

    // Access Token
    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    // Refresh Token
    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    // Username
    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    fun getUsername(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }
    }

    // User ID
    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    // User Role
    suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
        }
    }

    fun getUserRole(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }
    }

    // User Template
    suspend fun saveUserTemplate(template: String) {
        dataStore.edit { preferences ->
            preferences[USER_TEMPLATE_KEY] = template
        }
    }

    fun getUserTemplate(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_TEMPLATE_KEY]
        }
    }

    // User Language
    suspend fun saveUserLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[USER_LANGUAGE_KEY] = language
        }
    }

    fun getUserLanguage(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_LANGUAGE_KEY]
        }
    }

    // Session Timestamp
    suspend fun saveSessionTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[SESSION_TIMESTAMP_KEY] = timestamp
        }
    }

    fun getSessionTimestamp(): Flow<Long?> {
        return dataStore.data.map { preferences ->
            preferences[SESSION_TIMESTAMP_KEY]
        }
    }

    // Expires In
    suspend fun saveExpiresIn(expiresIn: String) {
        dataStore.edit { preferences ->
            preferences[EXPIRES_IN_KEY] = expiresIn
        }
    }

    fun getExpiresIn(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[EXPIRES_IN_KEY]
        }
    }

    // Token Type
    suspend fun saveTokenType(tokenType: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_TYPE_KEY] = tokenType
        }
    }

    fun getTokenType(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_TYPE_KEY]
        }
    }

    // Clear all session data
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(USER_TEMPLATE_KEY)
            preferences.remove(USER_LANGUAGE_KEY)
            preferences.remove(SESSION_TIMESTAMP_KEY)
            preferences.remove(EXPIRES_IN_KEY)
            preferences.remove(TOKEN_TYPE_KEY)
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null && preferences[SESSION_TIMESTAMP_KEY] != null
        }
    }

    // Check if session is expired (2 minutes)
    fun isSessionExpired(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val timestamp = preferences[SESSION_TIMESTAMP_KEY] ?: return@map true
            val currentTime = System.currentTimeMillis()
            val sessionDuration = 2 * 60 * 1000L // 2 minutos en milisegundos
            (currentTime - timestamp) > sessionDuration
        }
    }
}

