package com.klim.typeless.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingDoneKey = booleanPreferencesKey("onboarding_done")

    val isOnboardingDone: Flow<Boolean> = context.appDataStore.data
        .map { it[onboardingDoneKey] ?: false }

    suspend fun setOnboardingDone() {
        context.appDataStore.edit { it[onboardingDoneKey] = true }
    }
}