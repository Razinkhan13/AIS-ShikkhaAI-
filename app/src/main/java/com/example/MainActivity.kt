package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.ChapterDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MistakeVaultScreen
import com.example.ui.theme.ShikkhaAITheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ShikkhaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShikkhaAITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ShikkhaAIApp()
                }
            }
        }
    }
}

@Composable
fun ShikkhaAIApp(
    viewModel: ShikkhaViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedStepTab by viewModel.selectedStepTab.collectAsState()

    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val audioProgressSeconds by viewModel.audioProgressSeconds.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()

    val socraticMessages by viewModel.socraticMessages.collectAsState()
    val isSocraticLoading by viewModel.isSocraticLoading.collectAsState()

    val mcqSelectedAnswers by viewModel.mcqSelectedAnswers.collectAsState()
    val isMCQSubmitted by viewModel.isMCQSubmitted.collectAsState()
    val mcqScore by viewModel.mcqScore.collectAsState()

    val answerKa by viewModel.answerKa.collectAsState()
    val answerKha by viewModel.answerKha.collectAsState()
    val answerGa by viewModel.answerGa.collectAsState()
    val answerGha by viewModel.answerGha.collectAsState()
    val cqEvaluation by viewModel.cqEvaluation.collectAsState()
    val isCQEvaluating by viewModel.isCQEvaluating.collectAsState()

    val isCQSavedToVault by viewModel.isCQSavedToVault.collectAsState()

    val allMistakes by viewModel.allMistakes.collectAsState()
    val funnelStats by viewModel.funnelStats.collectAsState()

    val filteredChapters = CurriculumRepository.getChapters(selectedLevel, selectedSubject)
    val unresolvedCount = allMistakes.count { !it.isResolved }

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                selectedLevel = selectedLevel,
                selectedSubject = selectedSubject,
                chapters = filteredChapters,
                funnelStats = funnelStats,
                unresolvedMistakeCount = unresolvedCount,
                onSelectLevel = { viewModel.selectLevel(it) },
                onSelectSubject = { viewModel.selectSubject(it) },
                onOpenChapter = { viewModel.openChapter(it) },
                onOpenMistakeVault = { viewModel.navigateToMistakeVault() }
            )
        }

        AppScreen.CHAPTER_DETAIL -> {
            if (selectedChapter != null) {
                ChapterDetailScreen(
                    chapter = selectedChapter!!,
                    selectedStepTab = selectedStepTab,
                    onSelectStepTab = { viewModel.setStepTab(it) },
                    onBack = { viewModel.navigateToHome() },
                    // Audio
                    isPlayingAudio = isPlayingAudio,
                    audioProgressSeconds = audioProgressSeconds,
                    playbackSpeed = playbackSpeed,
                    onTogglePlayAudio = { viewModel.togglePlayPauseAudio() },
                    onSeekAudio = { viewModel.seekAudio(it) },
                    onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                    // Socratic Chat
                    socraticMessages = socraticMessages,
                    isSocraticLoading = isSocraticLoading,
                    onSendDoubt = { viewModel.sendSocraticDoubt(it) },
                    // Board Exam & CQ
                    mcqSelectedAnswers = mcqSelectedAnswers,
                    isMCQSubmitted = isMCQSubmitted,
                    mcqScore = mcqScore,
                    onSelectMCQOption = { qIdx, optIdx -> viewModel.selectMCQAnswer(qIdx, optIdx) },
                    onSubmitMCQ = { viewModel.submitMCQQuiz() },
                    onResetMCQs = { viewModel.resetMCQQuiz() },
                    answerKa = answerKa,
                    answerKha = answerKha,
                    answerGa = answerGa,
                    answerGha = answerGha,
                    onUpdateKa = { viewModel.updateAnswerKa(it) },
                    onUpdateKha = { viewModel.updateAnswerKha(it) },
                    onUpdateGa = { viewModel.updateAnswerGa(it) },
                    onUpdateGha = { viewModel.updateAnswerGha(it) },
                    onEvaluateCQ = { viewModel.evaluateCreativeQuestion() },
                    isCQEvaluating = isCQEvaluating,
                    cqEvaluation = cqEvaluation,
                    isCQSavedToVault = isCQSavedToVault,
                    onSaveToMistakeVault = { viewModel.flagCQToMistakeVault() }
                )
            } else {
                viewModel.navigateToHome()
            }
        }

        AppScreen.MISTAKE_VAULT -> {
            MistakeVaultScreen(
                mistakes = allMistakes,
                onToggleResolved = { viewModel.toggleMistakeResolved(it) },
                onDeleteMistake = { viewModel.deleteMistake(it) },
                onBack = { viewModel.navigateToHome() }
            )
        }
    }
}
