package com.cyberfusion.ui.features.more

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink2
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.TextLo

private val Sky = Color(0xFF60A5FA)
private val Coral = Color(0xFFFB7185)
private val Amber = Color(0xFFFBBF24)
private val Mint = Color(0xFF34D399)
private val Violet = Color(0xFFA78BFA)

@Composable
fun MoreScreen(navController: NavController) {
    val operations = listOf(
        MoreItem("Alerts", Icons.Default.Notifications, "alerts", Cyan),
        MoreItem("Investigations", Icons.Default.Search, "investigations", Sky),
        MoreItem("Incidents", Icons.Default.Warning, "incidents", Coral),
        MoreItem("GRC", Icons.Default.Security, "grc", Amber),
        MoreItem("Reports", Icons.Default.Description, "reports", Mint)
    )
    val system = listOf(
        MoreItem("Tools", Icons.Default.Build, "tools", Violet),
        MoreItem("AI Models", Icons.Default.Computer, "ai_models", Cyan),
        MoreItem("Diagnostics", Icons.Default.Terminal, "diagnostics", Mint),
        MoreItem("Settings", Icons.Default.Settings, "settings", TextLo)
    )

    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("CONTROL CENTER", style = MaterialTheme.typography.labelSmall, color = Cyan)
                Text("More", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { SectionHeader("Operations") }
                items(operations.size) { index -> MoreRow(operations[index]) { navController.navigate(operations[index].route) } }
                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHeader("System")
                }
                items(system.size) { index -> MoreRow(system[index]) { navController.navigate(system[index].route) } }
                item { Spacer(Modifier.height(4.dp)) }
            }
            CyberFusionBottomBar(navController)
        }
    }
}

@Composable
private fun MoreRow(item: MoreItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Ink2.copy(alpha = 0.9f))
            .border(1.dp, Ink4.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(item.accent.copy(alpha = 0.14f))
                .border(1.dp, item.accent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = item.accent, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(item.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextLo,
            modifier = Modifier.size(16.dp)
        )
    }
}

data class MoreItem(val title: String, val icon: ImageVector, val route: String, val accent: Color)
