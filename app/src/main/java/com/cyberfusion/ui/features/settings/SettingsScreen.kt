package com.cyberfusion.ui.features.settings

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink3
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.Violet
import kotlinx.coroutines.launch

private val Ink0Button = Color(0xFF070B14)
private val CoralButton = Color(0xFFFB7185)

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val uiState by viewModel.uiState.collectAsState()

    ScreenScaffold(
        title = "Settings",
        subtitle = "AI providers and threat intelligence APIs",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(Modifier.padding(top = 6.dp)) {
                    SectionHeader("AI Providers", subtitle = "Cloud models used by the agent")
                    Spacer(Modifier.height(6.dp))
                }
            }
            item { RaxCallout() }
            items(uiState.providers.size) { index ->
                ProviderCard(provider = uiState.providers[index], viewModel = viewModel)
            }
            item {
                Column(Modifier.padding(top = 10.dp)) {
                    SectionHeader("Cybersecurity APIs", subtitle = "Enrichment sources for IOCs and CVEs")
                    Spacer(Modifier.height(6.dp))
                }
            }
            items(uiState.apis.size) { index ->
                ApiCard(api = uiState.apis[index], viewModel = viewModel)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun RaxCallout() {
    Panel(borderColor = Cyan.copy(alpha = 0.45f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Rax AI · primary engine",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextHi
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "The agent runs on Rax AI with tool calling and long-term memory. Pick rax-4.0 for speed or rax-4.5 for deep 262K-context reasoning. Free key at ai.raxcore.dev.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLo
                )
            }
        }
    }
}

@Composable
private fun ProviderCard(provider: ProviderSettings, viewModel: SettingsViewModel) {
    var apiKey by remember(provider.id) { mutableStateOf(provider.apiKey) }
    var model by remember(provider.id) { mutableStateOf(provider.model) }
    var modelMenuOpen by remember(provider.id) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val connected = provider.status == "Connected"
    val isRax = provider.id == "rax"

    Panel(borderColor = if (isRax) Cyan.copy(alpha = 0.35f) else Ink4.copy(alpha = 0.55f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Psychology,
                contentDescription = null,
                tint = if (provider.isEnabled) (if (isRax) Cyan else Mint) else TextLo,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                provider.name + if (isRax) "  ★" else "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            StatusChip(provider.status, if (connected) Mint else TextLo)
        }
        Spacer(Modifier.height(10.dp))
        CyberTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = if (isRax) "Rax API key (rax_…)" else "API Key",
            isPassword = true
        )
        Spacer(Modifier.height(8.dp))

        if (isRax) {
            // Model selector with both Rax models available for selection.
            ExposedDropdownMenuBox(
                expanded = modelMenuOpen,
                onExpandedChange = { modelMenuOpen = it }
            ) {
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model", color = TextLo) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan.copy(alpha = 0.6f),
                        unfocusedBorderColor = Ink4,
                        focusedContainerColor = Ink3,
                        unfocusedContainerColor = Ink3,
                        cursorColor = Cyan,
                        focusedTextColor = TextHi,
                        unfocusedTextColor = TextHi
                    )
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = modelMenuOpen,
                    onDismissRequest = { modelMenuOpen = false }
                ) {
                    RAX_MODELS.forEach { candidate ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(candidate.id, color = TextHi, style = MaterialTheme.typography.titleSmall)
                                    Text(candidate.blurb, color = TextLo, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                model = candidate.id
                                modelMenuOpen = false
                            }
                        )
                    }
                }
            }
            if (model == "rax-4.0") {
                Text(
                    "rax-4.0 — open-source workhorse, sub-50ms, best for real-time triage.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLo,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (model == "rax-4.5") {
                Text(
                    "rax-4.5 — flagship deep thinker, 262K context, best for long investigations.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Violet,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            CyberTextField(
                value = model,
                onValueChange = { model = it },
                label = "Model"
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    viewModel.updateProviderApiKey(provider.id, apiKey)
                    viewModel.updateProviderModel(provider.id, model)
                    viewModel.saveProviderSettings(provider.copy(apiKey = apiKey, model = model, isEnabled = true))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Ink0Button)
            ) {
                Text("Save")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    viewModel.updateProviderApiKey(provider.id, apiKey)
                    viewModel.updateProviderModel(provider.id, model)
                    viewModel.testProviderConnection(provider.id)
                }
            }) {
                Text("Test")
            }
            if (provider.isEnabled) {
                OutlinedButton(onClick = {
                    viewModel.saveProviderSettings(provider.copy(isEnabled = false))
                }) {
                    Text("Disable")
                }
            }
        }
    }
}

@Composable
private fun ApiCard(api: ApiSettings, viewModel: SettingsViewModel) {
    var apiKey by remember(api.id) { mutableStateOf(api.apiKey) }
    val connected = api.status == "Connected"

    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Key,
                contentDescription = null,
                tint = if (api.isEnabled) Cyan else TextLo,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                api.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            StatusChip(api.status, if (connected) Mint else TextLo)
        }
        Spacer(Modifier.height(10.dp))
        CyberTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = "API Key",
            isPassword = true
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    viewModel.updateApiKey(api.id, apiKey)
                    viewModel.saveApiSettings(api.copy(apiKey = apiKey))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Ink0Button)
            ) {
                Text("Save")
            }
            OutlinedButton(onClick = {
                viewModel.updateApiKey(api.id, apiKey)
                viewModel.testApiConnection(api.id)
            }) {
                Text("Test")
            }
        }
    }
}

@Composable
private fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextLo) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan.copy(alpha = 0.6f),
            unfocusedBorderColor = Ink4,
            focusedContainerColor = Ink3,
            unfocusedContainerColor = Ink3,
            cursorColor = Cyan,
            focusedTextColor = TextHi,
            unfocusedTextColor = TextHi
        )
    )
}

private data class RaxModelChoice(val id: String, val blurb: String)

private val RAX_MODELS = listOf(
    RaxModelChoice("rax-4.0", "Fast workhorse · sub-50ms · general triage"),
    RaxModelChoice("rax-4.5", "Deep thinker · 262K context · long investigations")
)
