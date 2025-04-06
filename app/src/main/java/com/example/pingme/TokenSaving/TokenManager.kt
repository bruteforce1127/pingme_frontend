package com.example.pingme.TokenSaving

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object TokenManager {
    private const val PREFERENCE_NAME = "user_prefs"
    private val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
    suspend fun deleteToken(context: Context){
        context.dataStore.edit {prefs->
            prefs.remove(TOKEN_KEY)
        }
    }
    suspend fun getToken(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN_KEY]
    }
}
