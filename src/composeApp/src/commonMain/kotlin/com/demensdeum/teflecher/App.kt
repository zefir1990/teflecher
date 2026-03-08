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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width

enum class Language {
    EN, RU
}

data class AppStrings(
    val selectQuizJson: String,
    val loadQuizJson: String,
    val invalidFormat: String,
    val correct: String,
    val incorrect: String,
    val nextQuestion: String,
    val quizCompleted: String,
    val youScored: (Int, Int) -> String,
    val restartQuiz: String,
    val retryMistakes: String,
    val retryWrongAnswersButton: String
)

val enStrings = AppStrings(
    selectQuizJson = "Select Quiz JSON to Load",
    loadQuizJson = "Load Quiz JSON",
    invalidFormat = "Invalid Quiz format:",
    correct = "Correct!",
    incorrect = "Incorrect!",
    nextQuestion = "Next Question",
    quizCompleted = "Quiz Completed!",
    youScored = { score, total -> "You scored $score out of $total!" },
    restartQuiz = "Restart Quiz",
    retryMistakes = "(Retry Mistakes)",
    retryWrongAnswersButton = "Retry Wrong Answers"
)

val ruStrings = AppStrings(
    selectQuizJson = "Выберите JSON файл для загрузки",
    loadQuizJson = "Загрузить Викторину JSON",
    invalidFormat = "Неверный формат викторины:",
    correct = "Правильно!",
    incorrect = "Неправильно!",
    nextQuestion = "Следующий Вопрос",
    quizCompleted = "Викторина Завершена!",
    youScored = { score, total -> "Вы набрали $score из $total!" },
    restartQuiz = "Начать Заново",
    retryMistakes = "(Работа над ошибками)",
    retryWrongAnswersButton = "Пройти Ошибки"
)
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
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var wrongAnsweredQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
        var selectedLanguage by remember { mutableStateOf(Language.EN) }
        val strings = if (selectedLanguage == Language.EN) enStrings else ruStrings
        val coroutineScope = rememberCoroutineScope()
        
        if (quiz == null) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Row {
                    Button(
                        onClick = { selectedLanguage = Language.EN },
                        colors = if (selectedLanguage == Language.EN) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.buttonColors()
                    ) {
                        Text("English", color = if (selectedLanguage == Language.EN) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { selectedLanguage = Language.RU },
                        colors = if (selectedLanguage == Language.RU) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.buttonColors()
                    ) {
                        Text("Русский", color = if (selectedLanguage == Language.RU) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(strings.selectQuizJson, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        errorMessage = null
                        val jsonFileContent = loadQuizFile()
                        if (jsonFileContent != null) {
                            try {
                                val loadedQuiz = Json.decodeFromString<Quiz>(jsonFileContent)
                                
                                if (loadedQuiz.questions.isEmpty()) {
                                    throw Exception("Quiz contains no questions.")
                                }
                                loadedQuiz.questions.forEach { question ->
                                    if (question.answers.size < 2) {
                                        throw Exception("Question '${question.text}' has fewer than 2 answers.")
                                    }
                                    val correctAnswersCount = question.answers.count { it.isCorrect }
                                    if (correctAnswersCount == 0) {
                                        throw Exception("Question '${question.text}' has no correct answers.")
                                    }
                                }

                                quiz = loadedQuiz
                                currentQuestionIndex = 0
                                selectedAnswer = null
                                correctAnswersCount = 0
                                wrongAnsweredQuestions = emptyList()
                            } catch (e: Exception) {
                                println("Failed to decode or validate JSON: ${e.message}")
                                errorMessage = "${strings.invalidFormat} ${e.message}"
                            }
                        } else {
                            // Fallback if file picker is not implemented for the target
                            quiz = Json.decodeFromString<Quiz>(hardcodedQuizJson)
                            currentQuestionIndex = 0
                            selectedAnswer = null
                            correctAnswersCount = 0
                            wrongAnsweredQuestions = emptyList()
                        }
                    }
                }) {
                    Text(strings.loadQuizJson)
                }
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                    val randomizedAnswers = remember(currentQuestion) { currentQuestion.answers.shuffled() }

                    Text(text = currentQuestion.text, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    randomizedAnswers.forEach { answer ->
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
                            text = if (selectedAnswer!!.isCorrect) strings.correct else strings.incorrect,
                            color = if (selectedAnswer!!.isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            if (selectedAnswer?.isCorrect == true) {
                                correctAnswersCount++
                            } else {
                                currentQuestion?.let {
                                    wrongAnsweredQuestions = wrongAnsweredQuestions + it
                                }
                            }
                            currentQuestionIndex++
                            selectedAnswer = null
                        }) {
                            Text(strings.nextQuestion)
                        }
                    }
                } else {
                    Text(strings.quizCompleted, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(strings.youScored(correctAnswersCount, validQuiz.questions.size), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        quiz = null
                    }) {
                        Text(strings.restartQuiz)
                    }
                    if (wrongAnsweredQuestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val retryQuiz = Quiz(
                                id = validQuiz.id + "-retry",
                                title = validQuiz.title + " ${strings.retryMistakes}",
                                questions = wrongAnsweredQuestions
                            )
                            quiz = retryQuiz
                            currentQuestionIndex = 0
                            correctAnswersCount = 0
                            selectedAnswer = null
                            wrongAnsweredQuestions = emptyList()
                        }) {
                            Text(strings.retryWrongAnswersButton)
                        }
                    }
                }
            }
        }
    }
}