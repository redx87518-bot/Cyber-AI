package com.cyberfusion.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.TextLo

/**
 * Standard sub-screen chrome: gradient background, mono overline + title top bar,
 * optional back navigation and bottom bar. Content is centered when empty.
 */
@Composable
fun ScreenScaffold(
    title: String,
    subtitle: String? = null,
    navController: NavController? = null,
    showBottomBar: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            // Top bar
            Column(Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 6.dp)) {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                    }
                    Column {
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Cyan,
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    androidx.compose.foundation.layout.Box(
                        Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) { actions() }
                }
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLo,
                        modifier = Modifier.padding(start = if (navController != null) 56.dp else 16.dp, top = 2.dp)
                    )
                }
            }
            Box(Modifier.weight(1f)) { content() }
            if (showBottomBar && navController != null) {
                CyberFusionBottomBar(navController)
            }
        }
    }
}
