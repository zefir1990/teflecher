package com.demensdeum.teflecher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.launch
@Serializable
data class Answer(val id: String, val text: String, val isCorrect: Boolean)

@Serializable
data class Question(val id: String, val text: String, val answers: List<Answer>)

@Serializable
data class Quiz(val id: String, val title: String, val questions: List<Question>)

val hardcodedQuizJson = """
{
  "id": "quiz-001",
  "title": "Kotlin Basics",
  "questions": [
    {
      "id": "question-001",
      "text": "What is the keyword to declare a read-only variable in Kotlin?",
      "answers": [
        {"id": "answer-001", "text": "var", "isCorrect": false},
        {"id": "answer-002", "text": "val", "isCorrect": true},
        {"id": "answer-003", "text": "let", "isCorrect": false}
      ]
    },
    {
      "id": "question-002",
      "text": "Is Kotlin fully interoperable with Java?",
      "answers": [
        {"id": "answer-004", "text": "Yes", "isCorrect": true},
        {"id": "answer-005", "text": "No", "isCorrect": false}
      ]
    }
  ]
}
"""

@Composable
@Preview
fun App() {
    MaterialTheme {
        var quiz by remember { mutableStateOf<Quiz?>(null) }
        var currentQuestionIndex by remember { mutableStateOf(0) }
        var selectedAnswer by remember { mutableStateOf<Answer?>(null) }
        var correctAnswersCount by remember { mutableStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        
        if (quiz == null) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text("Select Quiz JSON to Load", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        val jsonFileContent = loadQuizFile()
                        if (jsonFileContent != null) {
                            try {
                                quiz = Json.decodeFromString<Quiz>(jsonFileContent)
                                currentQuestionIndex = 0
                                selectedAnswer = null
                                correctAnswersCount = 0
                            } catch (e: Exception) {
                                println("Failed to decode JSON: ${e.message}")
                            }
                        } else {
                            // Fallback if file picker is not implemented for the target
                            quiz = Json.decodeFromString<Quiz>(hardcodedQuizJson)
                            currentQuestionIndex = 0
                            selectedAnswer = null
                            correctAnswersCount = 0
                        }
                    }
                }) {
                    Text("Load Quiz JSON")
                }
            }
        } else {
            val validQuiz = quiz!!
            val currentQuestion = validQuiz.questions.getOrNull(currentQuestionIndex)
    
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = validQuiz.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (currentQuestion != null) {
                    Text(text = currentQuestion.text, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    currentQuestion.answers.forEach { answer ->
                        Button(
                            onClick = {
                                if (selectedAnswer == null) {
                                    selectedAnswer = answer
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = if (selectedAnswer == answer) {
                                if (answer.isCorrect) ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
                                else ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Red
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) {
                            Text(text = answer.text)
                        }
                    }
                    
                    if (selectedAnswer != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedAnswer!!.isCorrect) "Correct!" else "Incorrect!",
                            color = if (selectedAnswer!!.isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            if (selectedAnswer?.isCorrect == true) {
                                correctAnswersCount++
                            }
                            currentQuestionIndex++
                            selectedAnswer = null
                        }) {
                            Text("Next Question")
                        }
                    }
                } else {
                    Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You scored $correctAnswersCount out of ${validQuiz.questions.size}!", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        quiz = null
                    }) {
                        Text("Restart Quiz")
                    }
                }
            }
        }
    }
}