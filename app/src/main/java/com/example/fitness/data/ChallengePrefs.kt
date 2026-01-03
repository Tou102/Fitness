package com.example.fitness.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property để truy cập DataStore từ Context
val Context.challengeDataStore: DataStore<Preferences> by preferencesDataStore(name = "challenge_prefs")

object ChallengeKeys {
    val IS_CHALLENGE_ACTIVE = booleanPreferencesKey("is_challenge_active")
}

// Optional: Helper functions để set/get (dễ dùng hơn trong ViewModel/UI)
suspend fun Context.setChallengeActive(isActive: Boolean) {
    challengeDataStore.edit { preferences ->
        preferences[ChallengeKeys.IS_CHALLENGE_ACTIVE] = isActive
    }
}

fun Context.isChallengeActiveFlow(): Flow<Boolean> = challengeDataStore.data
    .map { preferences ->
        preferences[ChallengeKeys.IS_CHALLENGE_ACTIVE] ?: false
    }