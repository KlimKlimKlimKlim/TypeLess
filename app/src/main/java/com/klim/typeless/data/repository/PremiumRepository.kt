package com.klim.typeless.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.klim.typeless.data.preferences.appDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val unlockedUntilKey = longPreferencesKey("unlocked_until")

    val unlockedUntil: Flow<Long> = context.appDataStore.data.map { prefs ->
        prefs[unlockedUntilKey] ?: 0L
    }

    val hasPremiumAccess: Flow<Boolean> = unlockedUntil.map { unlockedUntil ->
        unlockedUntil > System.currentTimeMillis()
    }

    suspend fun setUnlockUntil(timestamp: Long) {
        context.appDataStore.edit { prefs ->
            prefs[unlockedUntilKey] = timestamp
        }
    }

    suspend fun unlockForHours(hours: Int = 3) {
        val unlockUntil = System.currentTimeMillis() + hours * 60 * 60 * 1000L
        setUnlockUntil(unlockUntil)
    }

    suspend fun clearUnlock() {
        context.appDataStore.edit { prefs ->
            prefs[unlockedUntilKey] = 0L
        }
    }
}