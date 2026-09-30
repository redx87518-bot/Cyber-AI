package com.cyberfusion.ui.features.aimodels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.features.settings.SettingsViewModel
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.Violet
import kotlinx.coroutines.launch

private val Ink0Button = Color(0xFF070B14)

@Composable
fun AIModelsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current)) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    ScreenScaffold(
        title = "AI Models",
        subtitle = "Provider engine powering the agent",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Panel(borderColor = Cyan.copy(alpha = 0.45f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Rax AI — primary engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextHi
                            )
                            Text(
                                "Tool-calling agent with long-term memory. Switch models below or in Settings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextLo
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    RaxModelChip(
                        icon = Icons.Default.Bolt,
                        name = "rax-4.0",
                        blurb = "Open-source workhorse · sub-50ms · real-time triage",
                        accent = Cyan
                    )
                    Spacer(Modifier.height(6.dp))
                    RaxModelChip(
                        icon = Icons.Default.Psychology,
                        name = "rax-4.5",
                        blurb = "Flagship deep thinker · 262K context · long investigations",
                        accent = Violet
                    )
                }
            }

            items(uiState.providers.size) { index ->
                val provider = uiState.providers[index]
                val isRax = provider.id == "rax"
                Panel(borderColor = if (isRax) Cyan.copy(alpha = 0.4f) else Ink4.copy(alpha = 0.55f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isRax) Icons.Default.AutoAwesome
                            else if (provider.id == "openrouter") Icons.Default.Cloud else Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (provider.isEnabled) (if (isRax) Cyan else Mint) else TextLo,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(provider.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(provider.model, style = MaterialTheme.typography.bodySmall, color = TextLo)
                        }
                        StatusChip(provider.status, if (provider.status == "Connected") Mint else TextLo, dot = false)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.saveProviderSettings(provider.copy(isEnabled = !provider.isEnabled))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (provider.isEnabled) Coral else Cyan,
                                contentColor = Ink0Button
                            )
                        ) {
                            Text(if (provider.isEnabled) "Disable" else "Enable")
                        }
                        OutlinedButton(onClick = {
                            scope.launch { viewModel.testProviderConnection(provider.id) }
                        }) {
                            Text("Test")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RaxModelChip(icon: ImageVector, name: String, blurb: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(name, style = MaterialTheme.typography.labelLarge, color = TextHi)
            Text(blurb, style = MaterialTheme.typography.labelSmall, color = TextLo)
        }
    }
}
