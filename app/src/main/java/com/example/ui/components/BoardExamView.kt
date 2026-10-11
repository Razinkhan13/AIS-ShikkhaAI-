package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CQEvaluationResult
import com.example.data.model.CreativeQuestionItem
import com.example.data.model.MCQItem
import com.example.ui.theme.TacticalAmberSecondary
import com.example.ui.theme.TacticalCyanGlow
import com.example.ui.theme.TacticalSuccess

@Composable
fun BoardExamView(
    mcqs: List<MCQItem>,
    selectedAnswers: Map<Int, Int>,
    isMCQSubmitted: Boolean,
    mcqScore: Int,
    onSelectOption: (questionIndex: Int, optionIndex: Int) -> Unit,
    onSubmitMCQs: () -> Unit,
    onResetMCQs: () -> Unit,
    cq: CreativeQuestionItem,
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
    var examTab by remember { mutableStateOf(0) } // 0: MCQs, 1: Creative Question (CQ)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("board_exam_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab row: MCQ vs CQ
        TabRow(
            selectedTabIndex = examTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = TacticalCyanGlow,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = examTab == 0,
                onClick = { examTab = 0 },
                text = {
                    Text(
                        text = "১. বহু নির্বাচনী প্রশ্ন (MCQ)",
                        fontWeight = if (examTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = examTab == 1,
                onClick = { examTab = 1 },
                text = {
                    Text(
                        text = "২. সৃজনশীল প্রশ্ন (CQ - ১০ নম্বর)",
                        fontWeight = if (examTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        if (examTab == 0) {
            // --- MCQ QUIZ SECTION ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "অধ্যায়ভিত্তিক ডায়াগনস্টিক টেস্ট",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isMCQSubmitted) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (mcqScore >= 7) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "স্কোর: $mcqScore / ${mcqs.size}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mcqScore >= 7) TacticalCyanGlow else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    mcqs.forEachIndexed { qIdx, mcq ->
                        MCQQuestionCard(
                            questionIndex = qIdx,
                            mcq = mcq,
                            selectedOption = selectedAnswers[qIdx],
                            isSubmitted = isMCQSubmitted,
                            onSelectOption = { optIdx -> onSelectOption(qIdx, optIdx) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (!isMCQSubmitted) {
                        Button(
                            onClick = onSubmitMCQs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_mcq_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalCyanGlow)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("উত্তর জমা দিন ও মূল্যায়ন দেখুন", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onResetMCQs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reset_mcq_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("পুনরায় কুইজ অনুশীলন করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // --- CQ SECTION ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Stimulus (উদ্দীপক)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = TacticalCyanGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "বোর্ড পরীক্ষার উদ্দীপক:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalCyanGlow
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cq.stimulus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ক (জ্ঞানমূলক, ১ নম্বর)
                    CQInputField(
                        label = cq.questionKa,
                        badge = "জ্ঞানমূলক (১ নম্বর)",
                        value = answerKa,
                        onValueChange = onUpdateKa,
                        placeholder = "পাঠ্যবইয়ের নির্ভুল তথ্য বা সংজ্ঞাটি ১ বাক্যে লিখুন...",
                        tag = "cq_input_ka"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // খ (অনুধাবনমূলক, ২ নম্বর)
                    CQInputField(
                        label = cq.questionKha,
                        badge = "অনুধাবনমূলক (২ নম্বর)",
                        value = answerKha,
                        onValueChange = onUpdateKha,
                        placeholder = "প্রথম অংশে মূল বিষয় ও দ্বিতীয় অংশে কার্যকারণ ব্যাখ্যা (২ প্যারা)...",
                        tag = "cq_input_kha"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // গ (প্রয়োগমূলক, ৩ নম্বর)
                    CQInputField(
                        label = cq.questionGa,
                        badge = "প্রয়োগমূলক (৩ নম্বর)",
                        value = answerGa,
                        onValueChange = onUpdateGa,
                        placeholder = "উদ্দীপকের তথ্য সূত্রে বসিয়ে ৩ প্যারায় গাণিতিক বা যৌক্তিক সমাধান করুন...",
                        tag = "cq_input_ga"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ঘ (উচ্চতর দক্ষতা, ৪ নম্বর)
                    CQInputField(
                        label = cq.questionGha,
                        badge = "উচ্চতর দক্ষতা (৪ নম্বর)",
                        value = answerGha,
                        onValueChange = onUpdateGha,
                        placeholder = "উদ্দীপকের সার্বিক দিক বিচার ও তুলনামূলক সিদ্ধান্তমূলক বিশ্লেষণ (৪ প্যারা)...",
                        tag = "cq_input_gha"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onEvaluateCQ,
                        enabled = !isCQEvaluating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("evaluate_cq_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalCyanGlow)
                    ) {
                        if (isCQEvaluating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("বোর্ড প্রধান পরীক্ষক খাতা মূল্যায়ন করছেন...", color = Color.Black)
                        } else {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "খাতা মূল্যায়ন করুন (AI Chief Examiner Grade)",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // Evaluation Report Card
                    if (cqEvaluation != null) {
                        Spacer(modifier = Modifier.height(18.dp))
                        CQScoreReportCard(
                            result = cqEvaluation,
                            isSaved = isCQSavedToVault,
                            onSaveToMistakeVault = onSaveToMistakeVault
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MCQQuestionCard(
    questionIndex: Int,
    mcq: MCQItem,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "${questionIndex + 1}. ${mcq.question}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            mcq.options.forEachIndexed { optIdx, optText ->
                val isSelected = selectedOption == optIdx
                val isCorrect = optIdx == mcq.correctIndex

                val borderColor = when {
                    !isSubmitted && isSelected -> TacticalCyanGlow
                    isSubmitted && isCorrect -> TacticalSuccess
                    isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }

                val bgColor = when {
                    !isSubmitted && isSelected -> TacticalCyanGlow.copy(alpha = 0.15f)
                    isSubmitted && isCorrect -> TacticalSuccess.copy(alpha = 0.2f)
                    isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !isSubmitted) { onSelectOption(optIdx) }
                        .border(1.5.dp, borderColor, RoundedCornerShape(10.dp)),
                    color = bgColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val optionPrefix = when (optIdx) {
                            0 -> "ক"
                            1 -> "খ"
                            2 -> "গ"
                            else -> "ঘ"
                        }
                        Text(
                            text = "$optionPrefix.",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = borderColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "ব্যাখ্যা: ${mcq.explanation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CQInputField(
    label: String,
    badge: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    tag: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(tag),
            minLines = 2,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun CQScoreReportCard(
    result: CQEvaluationResult,
    isSaved: Boolean,
    onSaveToMistakeVault: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TacticalCyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "এনসিটিবি মার্কিং শিট",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TacticalCyanGlow
                ) {
                    Text(
                        text = "${result.totalScore} / ১০",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = result.overallExaminerNote,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = TacticalCyanGlow
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Sub-scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ক (জ্ঞান): ${result.scoreKa}/১", style = MaterialTheme.typography.labelSmall)
                Text("খ (অনুধাবন): ${result.scoreKha}/২", style = MaterialTheme.typography.labelSmall)
                Text("গ (প্রয়োগ): ${result.scoreGa}/৩", style = MaterialTheme.typography.labelSmall)
                Text("ঘ (উচ্চতর): ${result.scoreGha}/৪", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ফিডব্যাক: ${result.feedbackGha}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (result.missingKeywords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "অনুপস্থিত কি-ওয়ার্ড: ${result.missingKeywords.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TacticalAmberSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isSaved) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TacticalSuccess.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TacticalSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ মিস্টেক ভল্টে সফলভাবে সংরক্ষিত হয়েছে",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = TacticalSuccess
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onSaveToMistakeVault,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_to_mistake_vault_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("দুর্বলতা হিসেবে মিস্টেক ভল্টে যোগ করুন")
                }
            }
        }
    }
}
