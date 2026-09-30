package com.cyberfusion.ui.features.ai

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.components.TypingDots
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink2
import com.cyberfusion.ui.theme.Ink3
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.Violet
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberfusion.core.utils.PdfUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto-scroll to the newest message (or the typing indicator).
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        val target = uiState.messages.size + if (uiState.isLoading) 1 else 0
        if (target > 0) listState.animateScrollToItem(target - 1)
    }

    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            // ── Header ──────────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Cyan, Violet))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF070B14),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "CyberFusion AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (uiState.isLoading) "Analyzing & executing tools…" else "Autonomous security agent · online",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.isLoading) Cyan else Mint
                    )
                }
                IconButton(onClick = { viewModel.startNewChat() }) {
                    Icon(
                        Icons.Filled.PostAdd,
                        contentDescription = "New chat",
                        tint = TextLo
                    )
                }
            }

            // ── Messages ────────────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!uiState.isLoading && uiState.messages.size <= 1) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "How can I help, Analyst?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Ask me to investigate IOCs, triage alerts, assess risks or generate a full PDF report.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextLo,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            items(ChatViewModel.suggestions) { suggestion ->
                                SuggestionChip(suggestion) {
                                    input = suggestion
                                }
                            }
                        }
                    }
                }

                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(message)
                }

                if (uiState.isLoading) {
                    item {
                        Row(verticalAlignment = Alignment.Bottom) {
                            AgentAvatar()
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Ink2)
                                    .border(1.dp, Ink4, RoundedCornerShape(18.dp))
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                TypingDots()
                            }
                        }
                    }
                }

                uiState.lastReport?.let { report ->
                    item {
                        Panel(borderColor = Mint.copy(alpha = 0.4f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Mint,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "PDF report ready",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        report.reportId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextLo
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    val path = report.filePath
                                    val file = if (path != null) java.io.File(path) else null
                                    if (file != null && file.exists()) PdfUtils.openPdf(context, file)
                                }) {
                                    Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Open")
                                }
                                OutlinedButton(onClick = {
                                    val path = report.filePath
                                    val file = if (path != null) java.io.File(path) else null
                                    if (file != null && file.exists()) PdfUtils.sharePdf(context, file)
                                }) {
                                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Share")
                                }
                            }
                        }
                    }
                }
            }

            // ── Composer ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask the agent to investigate…", color = TextLo) },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (input.isNotBlank() && !uiState.isLoading) {
                            viewModel.sendMessage(input)
                            input = ""
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan.copy(alpha = 0.6f),
                        unfocusedBorderColor = Ink4,
                        focusedContainerColor = Ink3,
                        unfocusedContainerColor = Ink3,
                        cursorColor = Cyan,
                        focusedTextColor = TextHi,
                        unfocusedTextColor = TextHi
                    )
                )
                Spacer(Modifier.width(8.dp))
                val canSend = input.isNotBlank() && !uiState.isLoading
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                uiState.isLoading -> Ink4
                                canSend -> Brush.linearGradient(listOf(Cyan, Violet))
                                else -> Brush.linearGradient(listOf(Ink4, Ink4))
                            }
                        )
                        .clickable(enabled = canSend) {
                            viewModel.sendMessage(input)
                            input = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (canSend) Color(0xFF070B14) else TextLo,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            CyberFusionBottomBar(navController)
        }
    }
}

@Composable
private fun SuggestionChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Ink2)
            .border(1.dp, Cyan.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = Cyan)
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            AgentAvatar()
            Spacer(Modifier.width(8.dp))
        }
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isUser) 18.dp else 6.dp,
                            topEnd = if (isUser) 6.dp else 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(
                        if (isUser) Brush.linearGradient(listOf(Color(0xFF1B4B5A), Color(0xFF14324A)))
                        else Brush.linearGradient(listOf(Ink2, Ink3))
                    )
                    .border(
                        1.dp,
                        if (isUser) Cyan.copy(alpha = 0.35f) else Ink4,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHi
                )
            }
            Text(
                text = timeFormat.format(Date(message.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = TextLo.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 3.dp, start = 6.dp, end = 6.dp)
            )
        }
        if (isUser) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Ink3)
                    .border(1.dp, Ink4, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "A",
                    style = MaterialTheme.typography.labelMedium,
                    color = Cyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AgentAvatar() {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Cyan, Violet))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFF070B14),
            modifier = Modifier.size(16.dp)
        )
    }
}
