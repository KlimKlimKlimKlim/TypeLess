package com.klim.typeless.ui.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onBack: (() -> Unit)? = null,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adState by viewModel.adState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Unlock")
                },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text(text = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        PaywallContent(
            innerPadding = innerPadding,
            uiState = uiState,
            isAdLoading = adState.isLoading,
            hasAdError = adState.hasError,
            onUnlockClick = {
                val activity = context as? android.app.Activity
                if (activity != null) {
                    viewModel.onWatchAdClick(activity)
                }
            }
        )
    }
}

@Composable
private fun PaywallContent(
    innerPadding: PaddingValues,
    uiState: PaywallUiState,
    isAdLoading: Boolean,
    hasAdError: Boolean,
    onUnlockClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (uiState.isUnlocked) "Unlocked" else "Temporary unlock",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = if (uiState.isUnlocked) {
                        "All free limits are temporarily disabled."
                    } else {
                        "Watch a rewarded ad to disable all free limits for 3 hours."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (uiState.isUnlocked) {
                    Text(
                        text = "Time left: ${formatDuration(uiState.remainingMillis)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (hasAdError) {
                    Text(
                        text = "Ad is not ready yet. Try again in a moment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Free limits",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "• Up to 5 snippets",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "• Only General folder",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "• Arguments are unavailable",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "• All limits are removed for 3 hours after rewarded ad",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = onUnlockClick,
            enabled = !isAdLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isAdLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp))
            } else {
                Text(
                    text = if (uiState.isUnlocked) {
                        "Extend unlock for 3 hours"
                    } else {
                        "Watch ad to unlock for 3 hours"
                    }
                )
            }
        }

        Text(
            text = "Rewarded ad required to unlock all limits.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}