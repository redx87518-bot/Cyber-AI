package com.cyberfusion.ui.features.investigations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.severityColor
import com.cyberfusion.ui.theme.statusColor

private data class InvestigationItem(
    val title: String,
    val status: String,
    val severity: String,
    val updated: String
)

@Composable
fun InvestigationsScreen(navController: NavController) {
    val investigations = listOf(
        InvestigationItem("APT29 Infrastructure", "In Progress", "High", "2 days ago"),
        InvestigationItem("Phishing Campaign Q3", "Open", "Medium", "1 week ago")
    )

    ScreenScaffold(
        title = "Investigations",
        subtitle = "Ongoing case work",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(investigations.size) { index ->
                val inv = investigations[index]
                Panel(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            inv.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))
                        StatusChip(inv.severity, severityColor(inv.severity))
                    }
                    Spacer(Modifier.padding(top = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusChip(inv.status, statusColor(inv.status))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            inv.updated,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLo
                        )
                    }
                }
            }
        }
    }
}
