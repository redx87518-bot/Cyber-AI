package com.cyberfusion.ui.features.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.theme.Gold
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavController) {
    var messages by remember { mutableStateOf(listOf(ChatMessage("ai", "Hello, Analyst. How can I assist you today?"))) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CyberFusion AI", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isUser) Gold else MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = msg.content,
                                modifier = Modifier.padding(12.dp),
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask CyberFusion AI...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (input.isNotBlank()) {
                        messages = messages + ChatMessage("user", input)
                        val userInput = input
                        input = ""
                        scope.launch {
                            messages = messages + ChatMessage("ai", "I received: $userInput. AI task execution is not yet configured.")
                        }
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Gold)
                }
            }
        }
    }
}

data class ChatMessage(val role: String, val content: String)