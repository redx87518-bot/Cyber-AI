package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.navigation.Screen
import com.cyberfusion.ui.theme.Amber
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.Violet

private fun difficultyColor(difficulty: String): Color = when (difficulty.lowercase()) {
    "beginner" -> Mint
    "intermediate" -> Cyan
    "advanced" -> Coral
    else -> TextLo
}

private fun trackColor(category: String): Color = when {
    category.contains("phishing", true) || category.contains("soc", true) -> Cyan
    category.contains("cloud", true) -> Violet
    category.contains("incident", true) -> Coral
    category.contains("hunting", true) || category.contains("intel", true) -> Amber
    category.contains("ethical", true) -> Color(0xFF60A5FA)
    category.contains("malware", true) -> Color(0xFFFB923C)
    category.contains("forensics", true) -> Mint
    else -> Cyan
}

@Composable
fun LabsScreen(
    navController: NavController,
    viewModel: LabsViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reload progress when returning from a lab (attempts update scores).
    val backStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(backStackEntry) { viewModel.refresh() }

    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("TRAINING GROUNDS", style = MaterialTheme.typography.labelSmall, color = Cyan)
                Text("Cyber Labs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (uiState.total > 0) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { uiState.progressFraction },
                            modifier = Modifier.weight(1f),
                            color = Mint,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "${uiState.completed}/${uiState.total} mastered",
                            style = MaterialTheme.typography.labelMedium,
                            color = Mint
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.labs.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyState(
                            icon = Icons.Filled.Science,
                            title = "Loading labs…",
                            hint = "Preparing SOC, cloud, forensics, hunting and red-team scenarios.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                val grouped = uiState.labs.groupBy { it.category }
                grouped.forEach { (category, labs) ->
                    item(key = "header_$category") {
                        Column(Modifier.padding(top = 8.dp)) {
                            SectionHeader(category, subtitle = "${labs.size} scenario${if (labs.size == 1) "" else "s"}")
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    items(labs.size, key = { idx -> "lab_${labs[idx].id}" }) { idx ->
                        val lab = labs[idx]
                        LabCard(lab) { navController.navigate(Screen.labDetail(lab.id)) }
                    }
                }
                item { Spacer(Modifier.height(4.dp)) }
            }

            CyberFusionBottomBar(navController)
        }
    }
}

@Composable
private fun LabCard(lab: LabUiItem, onClick: () -> Unit) {
    val accent = trackColor(lab.category)
    val diffColor = difficultyColor(lab.difficulty)
    val done = lab.progress?.completed == true

    Panel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = if (done) Mint.copy(alpha = 0.45f) else accent.copy(alpha = 0.3f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Science, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    lab.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    lab.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLo,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(lab.difficulty, diffColor, dot = false)
                    if (done) {
                        StatusChip("score ${lab.progress!!.score}%", Mint)
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (done) Mint else accent.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
