package com.klim.typeless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.klim.typeless.ui.navigation.AppNavigation
import com.klim.typeless.ui.settings.SettingsViewModel
import com.klim.typeless.ui.theme.AppTheme
import com.klim.typeless.ui.theme.TypeLessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appTheme by settingsViewModel.appTheme
                .collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)

            TypeLessTheme(appTheme = appTheme) {
                AppNavigation()
            }
        }
    }
}