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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.features.settings.SettingsViewModel
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink2
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextLo
import kotlinx.coroutines.launch

@Composable
fun AIModelsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current)) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    ScreenScaffold(
        title = "AI Models",
        subtitle = "Configure providers, models and fallbacks",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.providers.size) { index ->
                val provider = uiState.providers[index]
                val accent = if (provider.isEnabled) Mint else TextLo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Ink2.copy(alpha = 0.9f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (provider.id == "openrouter") Icons.Default.Cloud else Icons.Default.Computer,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(provider.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(provider.model, style = MaterialTheme.typography.bodySmall, color = TextLo)
                        }
                        StatusChip(provider.status, if (provider.status == "Connected") Mint else TextLo)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.saveProviderSettings(provider.copy(isEnabled = !provider.isEnabled))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (provider.isEnabled) Coral else Cyan,
                                contentColor = Color(0xFF070B14)
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
