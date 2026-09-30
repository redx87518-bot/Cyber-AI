package com.cyberfusion.ui.features.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.core.logging.CyberFusionLogger
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.theme.Coral
import com.cyberfusion.ui.theme.TextLo

@Composable
fun DiagnosticsScreen(navController: NavController) {
    val logs = CyberFusionLogger.getLogs()

    ScreenScaffold(
        title = "Diagnostics",
        subtitle = "${logs.size} entries · latest last",
        navController = navController,
        actions = {
            IconButton(onClick = { CyberFusionLogger.clearLogs() }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear logs",
                    tint = TextLo
                )
            }
        }
    ) {
        if (logs.isEmpty()) {
            Text(
                "No logs recorded yet.",
                style = MaterialTheme.typography.bodySmall,
                color = TextLo,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val visible = logs.takeLast(100)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(visible.size) { index ->
                    val log = visible[index]
                    Text(
                        text = log,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (log.contains("ERROR", true)) Coral else TextLo,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
