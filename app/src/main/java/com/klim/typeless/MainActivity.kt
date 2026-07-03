package com.klim.typeless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.klim.typeless.data.billing.BillingManager
import com.klim.typeless.ui.navigation.AppNavigation
import com.klim.typeless.ui.settings.SettingsViewModel
import com.klim.typeless.ui.theme.AppTheme
import com.klim.typeless.ui.theme.TypeLessTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        billingManager.connect {
            lifecycleScope.launch {
                billingManager.restorePurchases()
            }
        }

        setContent {
            val appTheme by settingsViewModel.appTheme
                .collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)

            TypeLessTheme(appTheme = appTheme) {
                AppNavigation()
            }
        }
    }
}