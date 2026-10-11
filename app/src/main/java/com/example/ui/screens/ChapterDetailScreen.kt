package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CQEvaluationResult
import com.example.data.model.Chapter
import com.example.data.model.SocraticMessage
import com.example.ui.components.AudioPodcastCard
import com.example.ui.components.BoardExamView
import com.example.ui.components.MindmapViewer
import com.example.ui.components.SocraticChatView
import com.example.ui.theme.TacticalCyanGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailScreen(
    chapter: Chapter,
    selectedStepTab: Int,
    onSelectStepTab: (Int) -> Unit,
    onBack: () -> Unit,
    // Audio Player
    isPlayingAudio: Boolean,
    audioProgressSeconds: Int,
    playbackSpeed: Float,
    onTogglePlayAudio: () -> Unit,
    onSeekAudio: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    // Socratic Chat
    socraticMessages: List<SocraticMessage>,
    isSocraticLoading: Boolean,
    onSendDoubt: (String) -> Unit,
    // Board Exam & CQ
    mcqSelectedAnswers: Map<Int, Int>,
    isMCQSubmitted: Boolean,
    mcqScore: Int,
    onSelectMCQOption: (Int, Int) -> Unit,
    onSubmitMCQ: () -> Unit,
    onResetMCQs: () -> Unit,
    answerKa: String,
    answerKha: String,
    answerGa: String,
    answerGha: String,
    onUpdateKa: (String) -> Unit,
    onUpdateKha: (String) -> Unit,
    onUpdateGa: (String) -> Unit,
    onUpdateGha: (String) -> Unit,
    onEvaluateCQ: () -> Unit,
    isCQEvaluating: Boolean,
    cqEvaluation: CQEvaluationResult?,
    isCQSavedToVault: Boolean,
    onSaveToMistakeVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D1525)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ais_logo),
                                contentDescription = "AIS Logo",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = chapter.titleBn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "AIS • ${chapter.level.labelBn} • ${chapter.subject.labelBn} • অধ্যায় ${chapter.chapterNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chapter_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TacticalCyanGlow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 4-Step Funnel Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedStepTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = TacticalCyanGlow,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                val stepTitles = listOf(
                    "১. অডিও প্রাইমিং (১০ মি.)",
                    "২. কনসেপ্ট ও মাইন্ডম্যাপ (২০ মি.)",
                    "৩. লার্নএলএম ডাউট (১৫ মি.)",
                    "৪. স্ব-মূল্যায়ন ও পরীক্ষা (১৫ মি.)"
                )
                stepTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedStepTab == index,
                        onClick = { onSelectStepTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedStepTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("funnel_step_tab_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body based on active step
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedStepTab) {
                    0 -> {
                        // Step 1: Audio Priming (Podcast)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            AudioPodcastCard(
                                podcast = chapter.podcast,
                                isPlaying = isPlayingAudio,
                                progressSeconds = audioProgressSeconds,
                                playbackSpeed = playbackSpeed,
                                onTogglePlayPause = onTogglePlayAudio,
                                onSeek = onSeekAudio,
                                onSpeedChange = onSpeedChange
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    1 -> {
                        // Step 2: Concept Drill & Visual Mindmap
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            MindmapViewer(chapter = chapter)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    2 -> {
                        // Step 3: LearnLM Socratic Doubt Solver
                        SocraticChatView(
                            messages = socraticMessages,
                            isLoading = isSocraticLoading,
                            onSendMessage = onSendDoubt,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    3 -> {
                        // Step 4: Diagnostic Self-Test & CQ
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            BoardExamView(
                                mcqs = chapter.mcqs,
                                selectedAnswers = mcqSelectedAnswers,
                                isMCQSubmitted = isMCQSubmitted,
                                mcqScore = mcqScore,
                                onSelectOption = onSelectMCQOption,
                                onSubmitMCQs = onSubmitMCQ,
                                onResetMCQs = onResetMCQs,
                                cq = chapter.cq,
                                answerKa = answerKa,
                                answerKha = answerKha,
                                answerGa = answerGa,
                                answerGha = answerGha,
                                onUpdateKa = onUpdateKa,
                                onUpdateKha = onUpdateKha,
                                onUpdateGa = onUpdateGa,
                                onUpdateGha = onUpdateGha,
                                onEvaluateCQ = onEvaluateCQ,
                                isCQEvaluating = isCQEvaluating,
                                cqEvaluation = cqEvaluation,
                                isCQSavedToVault = isCQSavedToVault,
                                onSaveToMistakeVault = onSaveToMistakeVault
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}
