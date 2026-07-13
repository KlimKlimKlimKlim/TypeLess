package com.klim.typeless.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.klim.typeless.ui.components.YandexBannerAd
import com.klim.typeless.ui.editor.EditorScreen
import com.klim.typeless.ui.home.FolderScreen
import com.klim.typeless.ui.home.HomeScreen
import com.klim.typeless.ui.home.folderColorFromHex
import com.klim.typeless.ui.onboarding.OnboardingScreen
import com.klim.typeless.ui.onboarding.OnboardingViewModel
import com.klim.typeless.ui.paywall.PaywallScreen
import com.klim.typeless.ui.settings.SettingsScreen
import com.klim.typeless.ui.stats.StatsScreen
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Folder : Screen("folder/{folderName}/{colorHex}") {
        fun createRoute(folderName: String, colorHex: Long) =
            "folder/${URLEncoder.encode(folderName, "UTF-8")}/$colorHex"
    }
    data object Editor : Screen("editor/{snippetId}/{defaultFolder}") {
        fun createRoute(snippetId: Int = -1, defaultFolder: String = "General") =
            "editor/$snippetId/${URLEncoder.encode(defaultFolder, "UTF-8")}"
    }
    data object Settings : Screen("settings")
    data object Stats : Screen("stats")
    data object Paywall : Screen("paywall")
}

private val routesWithoutBanner = setOf(Screen.Onboarding.route)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isOnboardingDone by onboardingViewModel.isOnboardingDone.collectAsStateWithLifecycle(false)

    val startDestination = if (isOnboardingDone) Screen.Home.route else Screen.Onboarding.route

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.hierarchy?.firstOrNull()?.route
    val showBanner = currentRoute !in routesWithoutBanner

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Folder.route) { backStackEntry ->
                val folderName = backStackEntry.arguments?.getString("folderName")
                    ?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
                val colorHex = backStackEntry.arguments?.getString("colorHex")
                    ?.toLongOrNull() ?: 0xFFBBDEFB
                FolderScreen(
                    navController = navController,
                    folderName = folderName,
                    folderColor = folderColorFromHex(colorHex)
                )
            }
            composable(Screen.Editor.route) {
                EditorScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(Screen.Stats.route) {
                StatsScreen(navController = navController)
            }
            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        if (showBanner) {
            YandexBannerAd()
        }
    }
}