package com.cyberfusion.ui.features.reports

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.TextLo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("MMM d · HH:mm", Locale.getDefault())

@Composable
fun ReportsScreen(navController: NavController) {
    val viewModel: ReportsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val uiState by viewModel.uiState.collectAsState()

    ScreenScaffold(
        title = "Reports",
        subtitle = "Generated investigation and assessment reports",
        navController = navController
    ) {
        if (uiState.reports.isEmpty() && !uiState.isLoading) {
            EmptyState(
                icon = Icons.Default.Description,
                title = "No reports yet",
                hint = "Ask the AI agent to run an investigation and generate a PDF report.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.reports.size) { index ->
                    val report = uiState.reports[index]
                    Panel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    report.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    dateFormat.format(Date(report.createdAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLo
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            StatusChip(report.type, Cyan)
                        }
                    }
                }
            }
        }
    }
}
