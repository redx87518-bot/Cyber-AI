package com.cyberfusion.ui.features.ai
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Gold
import com.cyberfusion.core.utils.PdfUtils
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = LocalViewModelFactory.current)) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CyberFusion AI", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.sendMessage("Generate a PDF report of this conversation")
                            Toast.makeText(context, "Generating PDF report...", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Gold)
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.sendMessage("Start a new conversation")
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "New Chat", tint = Gold)
                    }
                }
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(uiState.messages) { msg ->
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
                if (uiState.isLoading) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Analyzing and executing tools...",
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                uiState.lastReport?.let { report ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("PDF Report Ready", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        val file = java.io.File(report.filePath ?: "")
                                        if (file.exists()) {
                                            PdfUtils.openPdf(context, file)
                                        } else {
                                            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Open")
                                    }
                                    OutlinedButton(onClick = {
                                        val file = java.io.File(report.filePath ?: "")
                                        if (file.exists()) {
                                            PdfUtils.sharePdf(context, file)
                                        } else {
                                            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            uiState.error?.let { error ->
                if (error.contains("image", ignoreCase = true) || error.contains("png", ignoreCase = true) || error.contains("jpg", ignoreCase = true)) {
                    Text(
                        text = "Image analysis is not supported yet. Please describe the image content in text so I can assist you.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
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
                    placeholder = { Text("Give me instructions, ask for analysis, request a report...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            scope.launch {
                                viewModel.sendMessage(input)
                                input = ""
                            }
                        }
                    },
                    enabled = !uiState.isLoading && input.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Gold)
                }
            }
        }
    }
}
