package com.klim.typeless.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.klim.typeless.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore("settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    val appTheme: Flow<AppTheme> = context.settingsDataStore.data.map { prefs ->
        AppTheme.valueOf(prefs[THEME_KEY] ?: AppTheme.SYSTEM.name)
    }

    suspend fun setTheme(theme: AppTheme) {
        context.settingsDataStore.edit { it[THEME_KEY] = theme.name }
    }
}