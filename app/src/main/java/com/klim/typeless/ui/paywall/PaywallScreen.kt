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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            onUnlockClick = viewModel::unlockForTesting
        )
    }
}

@Composable
private fun PaywallContent(
    innerPadding: PaddingValues,
    uiState: PaywallUiState,
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.isUnlocked) {
                    "Extend unlock for 3 hours"
                } else {
                    "Test unlock for 3 hours"
                }
            )
        }

        Text(
            text = "Temporary button for testing. Later it will be replaced with rewarded ad.",
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