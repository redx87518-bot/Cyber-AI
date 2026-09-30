package com.cyberfusion.ui.features.dashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.components.StatTile
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.components.navigateTab
import com.cyberfusion.ui.navigation.Screen
import com.cyberfusion.ui.theme.Amber
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.Violet
import com.cyberfusion.ui.theme.severityColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val state by viewModel.state.collectAsState()

    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
            ) {
            // ── Hero ────────────────────────────────────────────────────────
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Cyan, Violet)))
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "CYBERFUSION CONSOLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Cyan
                            )
                            Text(
                                "Good ${partOfDay()}, Analyst",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Panel(
                        borderColor = threatTint(state.criticalAlerts).copy(alpha = 0.5f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = threatTint(state.criticalAlerts)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    threatLabel(state.criticalAlerts, state.activeIncidents),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${state.criticalAlerts} critical alerts · ${state.activeIncidents} active incidents",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLo
                                )
                            }
                        }
                    }
                }
            }

            // ── Quick actions ───────────────────────────────────────────────
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader("Quick Actions")
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickAction("Investigate", Icons.Filled.Radar, Cyan, Modifier.weight(1f)) {
                            navController.navigateTab(Screen.ThreatIntel.route)
                        }
                        QuickAction("New chat", Icons.Filled.Bolt, Violet, Modifier.weight(1f)) {
                            navController.navigateTab(Screen.AI.route)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickAction("Labs", Icons.Filled.Science, Mint, Modifier.weight(1f)) {
                            navController.navigateTab(Screen.Labs.route)
                        }
                        QuickAction("Reports", Icons.Filled.FactCheck, Amber, Modifier.weight(1f)) {
                            navController.navigate(Screen.Reports.route)
                        }
                    }
                }
            }

            // ── Stats grid ──────────────────────────────────────────────────
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(18.dp))
                    SectionHeader("Live Posture", subtitle = "Counts update in real time")
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatTile("Alerts", state.alertsCount.toString(), Cyan, Modifier.weight(1f))
                        StatTile("Incidents", state.activeIncidents.toString(), Coral, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatTile("Open risks", state.openRisks.toString(), Amber, Modifier.weight(1f))
                        StatTile("Labs", state.labsCount.toString(), Mint, Modifier.weight(1f))
                    }
                }
            }

            // ── Recent alerts ───────────────────────────────────────────────
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(18.dp))
                    SectionHeader("Recent Alerts")
                    Spacer(Modifier.height(10.dp))
                }
            }
            if (state.recentAlerts.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Radar,
                        title = "No alerts yet",
                        hint = "Alerts ingested by your tools will appear here.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(state.recentAlerts.size) { index ->
                    val alert = state.recentAlerts[index]
                    AlertRow(
                        title = alert.title,
                        severity = alert.severity,
                        source = alert.source,
                        time = alert.createdAt,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { navController.navigate(Screen.Alerts.route) }
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { navController.navigate(Screen.Alerts.route) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "View all alerts",
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
private fun QuickAction(
    label: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Ink2Brush)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = TextHi)
    }
}

@Composable
private fun AlertRow(
    title: String,
    severity: String,
    source: String,
    time: Long,
    modifier: Modifier = Modifier
) {
    Panel(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    source,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLo
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(severity, severityColor(severity))
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLo,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private val Ink2Brush = Brush.verticalGradient(
    listOf(Color(0xFF101A30).copy(alpha = 0.95f), Color(0xFF101A30).copy(alpha = 0.7f))
)

private fun partOfDay(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "morning"
        in 12..17 -> "afternoon"
        in 18..22 -> "evening"
        else -> "night"
    }
}

private fun threatTint(critical: Int): Color =
    if (critical > 0) Coral else Amber

private fun threatLabel(critical: Int, incidents: Int): String = when {
    critical > 0 -> "Elevated threat level"
    incidents > 0 -> "Active incident response"
    else -> "All systems nominal"
}
