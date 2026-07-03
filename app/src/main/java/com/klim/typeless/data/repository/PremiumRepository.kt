package com.klim.typeless.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
    private val isPremiumKey = booleanPreferencesKey("is_premium")

    val isPremium: Flow<Boolean> = context.appDataStore.data.map { prefs ->
        prefs[isPremiumKey] ?: false
    }

    suspend fun setPremium(value: Boolean) {
        context.appDataStore.edit { prefs ->
            prefs[isPremiumKey] = value
        }
    }
}