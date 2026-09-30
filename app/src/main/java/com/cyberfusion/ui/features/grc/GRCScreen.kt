package com.cyberfusion.ui.features.grc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Amber
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.statusColor

private fun scoreColor(score: Int): Color = when {
    score >= 70 -> Coral
    score >= 40 -> Amber
    else -> Mint
}

@Composable
fun GRCScreen(navController: NavController) {
    val viewModel: GRCViewModel = viewModel(factory = LocalViewModelFactory.current)
    val uiState by viewModel.uiState.collectAsState()

    ScreenScaffold(
        title = "GRC",
        subtitle = "Governance, risk and compliance",
        navController = navController
    ) {
        if (uiState.risks.isEmpty() && !uiState.isLoading) {
            EmptyState(
                icon = Icons.Default.Security,
                title = "No risks recorded",
                hint = "Ask the AI agent to create GRC assessments, or add risks via tools.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.risks.size) { index ->
                    val risk = uiState.risks[index]
                    Panel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    risk.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Likelihood: ${risk.likelihood} · Impact: ${risk.impact}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLo
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            StatusChip("score ${risk.riskScore}", scoreColor(risk.riskScore))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusChip(risk.status, statusColor(risk.status))
                        }
                    }
                }
            }
        }
    }
}
