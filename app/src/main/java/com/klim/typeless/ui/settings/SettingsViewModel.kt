package com.klim.typeless.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.data.preferences.SettingsRepository
import com.klim.typeless.domain.usecase.ExportSnippetsUseCase
import com.klim.typeless.domain.usecase.ImportSnippetsUseCase
import com.klim.typeless.domain.usecase.ObserveUnlockStatusUseCase
import com.klim.typeless.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportSnippets: ExportSnippetsUseCase,
    private val importSnippets: ImportSnippetsUseCase,
    private val settingsRepository: SettingsRepository,
    observeUnlockStatus: ObserveUnlockStatusUseCase
) : ViewModel() {

    private val _serviceEnabled = MutableStateFlow(false)
    val serviceEnabled: StateFlow<Boolean> = _serviceEnabled.asStateFlow()

    private val _importResult = MutableStateFlow<ImportSnippetsUseCase.Result?>(null)
    val importResult: StateFlow<ImportSnippetsUseCase.Result?> = _importResult.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportSnippetsUseCase.Result?>(null)
    val exportResult: StateFlow<ExportSnippetsUseCase.Result?> = _exportResult.asStateFlow()

    val appTheme: StateFlow<AppTheme> = settingsRepository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppTheme.SYSTEM)

    val isUnlocked: StateFlow<Boolean> = observeUnlockStatus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun refreshServiceStatus() {
        _serviceEnabled.value = isAccessibilityServiceEnabled()
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun export(outputStream: OutputStream) {
        viewModelScope.launch {
            _exportResult.value = exportSnippets(outputStream)
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }

    fun import(inputStream: InputStream) {
        viewModelScope.launch {
            _importResult.value = importSnippets(inputStream)
        }
    }

    fun clearImportResult() {
        _importResult.value = null
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.split(":").any {
            it.contains("TypeLessAccessibilityService", ignoreCase = true)
        }
    }
}