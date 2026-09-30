package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.core.labs.LabEngine
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Ink4
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextHi
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.severityColor

@Composable
fun LabDetailScreen(
    navController: NavController,
    labId: Long,
    labsViewModel: LabsViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val labContent = labsViewModel.getLabContent(labId)
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    if (labContent == null) {
        ScreenScaffold(title = "Lab", navController = navController) {
            EmptyState(
                icon = Icons.Default.Science,
                title = "Lab not found",
                hint = "This scenario is no longer available.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        return
    }

    ScreenScaffold(
        title = labContent.title,
        subtitle = "${labContent.category} · ${labContent.difficulty}",
        navController = navController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Panel {
                    SectionHeader("Scenario")
                    Spacer(Modifier.height(8.dp))
                    Text(labContent.scenario, style = MaterialTheme.typography.bodyMedium, color = TextHi)
                }
            }
            if (!labContent.evidence.isNullOrBlank()) {
                item {
                    Panel(borderColor = Cyan.copy(alpha = 0.4f)) {
                        SectionHeader("Evidence")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            labContent.evidence!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLo,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
            items(labContent.questions.size) { qIndex ->
                val question = labContent.questions[qIndex]
                Panel {
                    Text(
                        "Q${qIndex + 1}. ${question.question}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    question.options.forEachIndexed { index, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedAnswers[question.id] == index,
                                onClick = { selectedAnswers = selectedAnswers + (question.id to index) }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(option, style = MaterialTheme.typography.bodyMedium, color = TextLo)
                        }
                    }
                    if (showResult) {
                        val isCorrect = selectedAnswers[question.id] == question.correctAnswer
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (isCorrect) "✓ Correct" else "✗ Incorrect — ${question.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) Mint else Color(0xFFFB7185)
                        )
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            score = LabEngine.calculateScore(labContent.questions, selectedAnswers)
                            showResult = true
                        },
                        enabled = selectedAnswers.size == labContent.questions.size,
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Color(0xFF070B14))
                    ) {
                        Text("Submit Answers")
                    }
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Back")
                    }
                }
            }
            if (showResult) {
                item {
                    Panel(borderColor = Mint.copy(alpha = 0.5f)) {
                        SectionHeader("Result")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Score: $score%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Mint
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(LabEngine.getFeedback(score), style = MaterialTheme.typography.bodyMedium, color = TextLo)
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
