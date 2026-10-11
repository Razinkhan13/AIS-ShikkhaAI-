package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AcademicLevel(val labelBn: String, val classRange: String) {
    SSC("এসএসসি", "নবম-দশম শ্রেণি"),
    HSC("এইচএসসি", "একাদশ-দ্বাদশ শ্রেণি")
}

enum class Subject(val labelBn: String, val iconName: String) {
    PHYSICS("পদার্থবিজ্ঞান", "bolt"),
    CHEMISTRY("রসায়ন", "science"),
    BIOLOGY("জীববিজ্ঞান", "eco"),
    BANGLA("বাংলা ১ম পত্র", "menu_book")
}

data class FormulaItem(
    val title: String,
    val formula: String,
    val unit: String,
    val explanation: String,
    val commonPitfall: String
)

data class MindmapNode(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val detail: String? = null,
    val children: List<MindmapNode> = emptyList()
)

data class TranscriptLine(
    val speaker: String,
    val text: String,
    val timeLabel: String,
    val timeSecond: Int
)

data class AudioPodcastInfo(
    val title: String,
    val episodeDurationSeconds: Int,
    val hostsDescription: String,
    val keyTakeaway: String,
    val transcript: List<TranscriptLine>
)

data class MCQItem(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class CreativeQuestionItem(
    val id: String,
    val stimulus: String,
    val questionKa: String,
    val marksKa: Int = 1,
    val modelAnswerKa: String,
    val questionKha: String,
    val marksKha: Int = 2,
    val modelAnswerKha: String,
    val questionGa: String,
    val marksGa: Int = 3,
    val modelAnswerGa: String,
    val questionGha: String,
    val marksGha: Int = 4,
    val modelAnswerGha: String
)

data class CQEvaluationResult(
    val totalScore: Int,
    val scoreKa: Int,
    val scoreKha: Int,
    val scoreGa: Int,
    val scoreGha: Int,
    val feedbackKa: String,
    val feedbackKha: String,
    val feedbackGa: String,
    val feedbackGha: String,
    val overallExaminerNote: String,
    val missingKeywords: List<String>
)

enum class SenderType {
    STUDENT,
    SOCRATIC_AI
}

data class SocraticMessage(
    val id: String,
    val sender: SenderType,
    val text: String,
    val banglishConverted: String? = null,
    val scaffoldingPrompt: String? = null,
    val retentionScenario: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class Chapter(
    val id: String,
    val level: AcademicLevel,
    val subject: Subject,
    val chapterNumber: Int,
    val titleBn: String,
    val subtitleBn: String,
    val squintEssence: List<String>,
    val analogyTitle: String,
    val analogyContent: String,
    val formulas: List<FormulaItem>,
    val podcast: AudioPodcastInfo,
    val mindmap: MindmapNode,
    val mcqs: List<MCQItem>,
    val cq: CreativeQuestionItem
)

@Entity(tableName = "mistake_vault")
data class MistakeVaultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterId: String,
    val chapterTitle: String,
    val subjectName: String,
    val questionType: String, // "MCQ" or "CQ"
    val questionSnippet: String,
    val studentAnswer: String,
    val rubricFeedback: String,
    val recommendedNCTBKeyword: String,
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_progress")
data class StudentProgressEntity(
    @PrimaryKey
    val chapterId: String,
    val isAudioCompleted: Boolean = false,
    val isMindmapCompleted: Boolean = false,
    val isDoubtSolverUsed: Boolean = false,
    val mcqScore: Int = 0,
    val mcqTotal: Int = 10,
    val cqScore: Int = 0,
    val isMastered: Boolean = false
)

data class FunnelStats(
    val questionsPracticed: Int,
    val weaknessesDiagnosed: Int,
    val chaptersMastered: Int
)
