package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold
import kotlinx.coroutines.launch

@Composable
fun LabDetailScreen(navController: NavController, labId: Long, labsViewModel: LabsViewModel = viewModel()) {
    val labContent = labsViewModel.getLabContent(labId)
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    
    if (labContent == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Lab Not Found") }) }) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Lab not found")
            }
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(labContent.title, fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text(labContent.scenario, style = MaterialTheme.typography.bodyMedium)
            }
            if (!labContent.evidence.isNullOrBlank()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Evidence", fontWeight = FontWeight.Bold, color = Gold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(labContent.evidence)
                        }
                    }
                }
            }
            items(labContent.questions) { question ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(question.question, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        question.options.forEachIndexed { index, option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedAnswers[question.id] == index,
                                    onClick = { selectedAnswers = selectedAnswers + (question.id to index) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(option)
                            }
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        score = com.cyberfusion.core.labs.LabEngine.calculateScore(labContent.questions, selectedAnswers)
                        showResult = true
                    }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                        Text("Submit Answers")
                    }
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Back")
                    }
                }
            }
            if (showResult) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Gold.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Score: $score%", fontWeight = FontWeight.Bold, color = Gold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(com.cyberfusion.core.labs.LabEngine.getFeedback(score))
                            Spacer(modifier = Modifier.height(8.dp))
                            labContent.questions.forEach { question ->
                                val userAnswer = selectedAnswers[question.id]
                                val isCorrect = userAnswer == question.correctAnswer
                                Text(
                                    text = if (isCorrect) "✓ Q${question.id}: Correct" else "✗ Q${question.id}: Incorrect - ${question.explanation}",
                                    color = if (isCorrect) Gold else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
