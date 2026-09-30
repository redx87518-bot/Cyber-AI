package com.cyberfusion.ui.features.tools

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.theme.Amber
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.Violet

private val Sky = Color(0xFF60A5FA)

private fun categoryAccent(category: String): Color = when (category) {
    "SOC" -> Cyan
    "Threat Intelligence" -> Violet
    "GRC" -> Amber
    "Vulnerability" -> Coral
    "Reporting" -> Mint
    "Labs" -> Sky
    else -> TextLo
}

@Composable
fun ToolsScreen(navController: NavController) {
    val tools = AIToolRegistry.tools
    val categories = tools.groupBy {
        when {
            it.name.contains("alert", true) || it.name.contains("incident", true) -> "SOC"
            it.name.contains("ioc", true) || it.name.contains("threat", true) || it.name.contains("malware", true) ||
                it.name.contains("abuse", true) || it.name.contains("otx", true) || it.name.contains("urlscan", true) ||
                it.name.contains("dns", true) || it.name.contains("rdap", true) || it.name.contains("whois", true) -> "Threat Intelligence"
            it.name.contains("grc", true) || it.name.contains("risk", true) || it.name.contains("iso", true) -> "GRC"
            it.name.contains("cve", true) || it.name.contains("mitre", true) || it.name.contains("vulnerability", true) -> "Vulnerability"
            it.name.contains("report", true) || it.name.contains("pdf", true) -> "Reporting"
            it.name.contains("lab", true) -> "Labs"
            it.name.contains("setting", true) -> "Utilities"
            else -> "General"
        }
    }

    ScreenScaffold(
        title = "Tools",
        subtitle = "${tools.size} agent tools available",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { (category, categoryTools) ->
                item(key = "header_$category") {
                    Column(Modifier.padding(top = 6.dp)) {
                        SectionHeader(category, subtitle = "${categoryTools.size} tools")
                        Spacer(Modifier.height(4.dp))
                    }
                }
                items(categoryTools.size, key = { idx -> "${category}_${categoryTools[idx].name}" }) { idx ->
                    val tool = categoryTools[idx]
                    val accent = categoryAccent(category)
                    Panel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(accent.copy(alpha = 0.14f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    tool.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    tool.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLo
                                )
                                if (tool.parameters.isNotEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "params: ${tool.parameters.keys.joinToString(", ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextLo.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
