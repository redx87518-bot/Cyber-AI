package com.cyberfusion.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cyberfusion.ui.navigation.Screen
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink2
import com.cyberfusion.ui.theme.TextFaint

private data class TabItem(val screen: Screen, val icon: ImageVector)

/**
 * Floating pill-style bottom navigation. Uses `popUpTo(start)` + `launchSingleTop`
 * so repeated taps don't grow the back stack, and `restoreState`/`saveState`
 * so tab state survives switching.
 */
@Composable
fun CyberFusionBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val items = listOf(
        TabItem(Screen.Dashboard, Icons.Filled.Home),
        TabItem(Screen.AI, Icons.Filled.Psychology),
        TabItem(Screen.ThreatIntel, Icons.Filled.Radar),
        TabItem(Screen.Labs, Icons.Filled.Science),
        TabItem(Screen.More, Icons.Filled.MoreHoriz)
    )

    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.screen.route
                val tint by animateColorAsState(
                    targetValue = if (selected) Cyan else TextFaint,
                    animationSpec = androidx.compose.animation.core.tween(200),
                    label = "tabTint"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Ink2 else Color.Transparent)
                        .border(
                            1.dp,
                            if (selected) Cyan.copy(alpha = 0.35f) else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigateTab(item.screen.route)
                        }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(item.icon, contentDescription = item.screen.title, tint = tint, modifier = Modifier.size(22.dp))
                    Text(
                        item.screen.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = tint
                    )
                }
            }
        }
    }
}

/**
 * Tab-safe navigation: dedupe + shallow back stack + state restore.
 */
fun NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
