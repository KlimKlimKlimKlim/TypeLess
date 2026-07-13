package com.klim.typeless.ui.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onBack: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Unlock")
                }
            )
        }
    ) { innerPadding ->
        PaywallContent(
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun PaywallContent(
    innerPadding: PaddingValues
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
                    text = "Temporary unlock",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "TypeLess is moving to a new monetization model. Permanent premium purchase has been removed.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Soon you will be able to watch rewarded ads and unlock all limits for 3 hours.",
                    style = MaterialTheme.typography.bodyMedium
                )
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
                    text = "Current free limits",
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
            }
        }

        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Rewarded ad coming soon")
        }

        Text(
            text = "This screen is temporarily disabled until rewarded ads are integrated.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}