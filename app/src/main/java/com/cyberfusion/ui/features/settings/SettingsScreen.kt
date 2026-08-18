package com.cyberfusion.ui.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Gold
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = LocalViewModelFactory.current)) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("AI Providers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold)
            }
            items(uiState.providers.size) { index ->
                ProviderCard(provider = uiState.providers[index], viewModel = viewModel)
            }
            item {
                Text("Cybersecurity APIs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold, modifier = Modifier.padding(top = 16.dp))
            }
            items(uiState.apis.size) { index ->
                ApiCard(api = uiState.apis[index], viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ProviderCard(provider: ProviderSettings, viewModel: SettingsViewModel) {
    var apiKey by remember { mutableStateOf(provider.apiKey) }
    var model by remember { mutableStateOf(provider.model) }
    val scope = rememberCoroutineScope()
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(provider.name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Surface(color = if (provider.status == "Connected") Gold else MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small) {
                    Text(provider.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    viewModel.updateProviderApiKey(provider.id, apiKey)
                    viewModel.updateProviderModel(provider.id, model)
                    viewModel.saveProviderSettings(provider.copy(apiKey = apiKey, model = model))
                }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
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
            }
        }
    }
}

@Composable
fun ApiCard(api: ApiSettings, viewModel: SettingsViewModel) {
    var apiKey by remember { mutableStateOf(api.apiKey) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(api.name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text(api.status, color = if (api.status == "Connected") Gold else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    viewModel.updateApiKey(api.id, apiKey)
                    viewModel.saveApiSettings(api.copy(apiKey = apiKey))
                }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
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
}