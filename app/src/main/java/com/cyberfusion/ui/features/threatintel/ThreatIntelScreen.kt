package com.cyberfusion.ui.features.threatintel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.components.TypingDots
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink3
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.severityColor

@Composable
fun ThreatIntelScreen(
    navController: NavController,
    viewModel: ThreatIntelViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    CyberBackground {
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    "THREAT INTEL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Cyan
                )
                Text(
                    "IOC Lookup",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.updateQuery(it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("IP, domain, hash, CVE…", color = TextLo) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = TextLo)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
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
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.search() },
                        enabled = !uiState.isLoading && query.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan,
                            contentColor = Color(0xFF070B14)
                        )
                    ) {
                        if (uiState.isLoading) {
                            TypingDots(color = Color(0xFF070B14))
                        } else {
                            Text("Scan")
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.error?.let { err ->
                    item {
                        Panel(borderColor = Color(0xFFFB7185).copy(alpha = 0.4f)) {
                            Text("Enrichment notice", color = Color(0xFFFB7185), fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(err, style = MaterialTheme.typography.bodySmall, color = TextLo)
                        }
                    }
                }

                if (uiState.results.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyState(
                            icon = Icons.Filled.Radar,
                            title = "No scans yet",
                            hint = "Enter an IP, domain, hash or CVE to enrich it from your connected intel sources.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                items(uiState.results.size) { index ->
                    val result = uiState.results[index]
                    ResultCard(result)
                }

                if (uiState.results.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { navController.navigate(com.cyberfusion.ui.navigation.Screen.AI.route) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ask the AI agent to analyze these results",
                                style = MaterialTheme.typography.labelLarge,
                                color = Cyan
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Cyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            CyberFusionBottomBar(navController)
        }
    }
}

@Composable
private fun ResultCard(result: ThreatIntelResult) {
    val accent = severityColor(result.reputation)
    Panel(modifier = Modifier.padding(horizontal = 0.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                result.ioc,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            StatusChip(result.source, Cyan)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("${result.type}", TextLo)
            StatusChip(result.reputation, accent)
            if (result.confidence > 0) {
                StatusChip("conf ${result.confidence}%", accent)
            }
        }
        result.details?.let { details ->
            Spacer(Modifier.height(8.dp))
            Text(details, style = MaterialTheme.typography.bodySmall, color = TextLo)
        }
    }
}
