package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ShikkhaDatabase
import com.example.data.model.*
import com.example.data.network.GeminiApiClient
import com.example.data.repository.CurriculumRepository
import com.example.data.repository.MistakeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    CHAPTER_DETAIL,
    MISTAKE_VAULT
}

class ShikkhaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ShikkhaDatabase.getDatabase(application)
    private val repository = MistakeRepository(db.shikkhaDao())

    // --- Navigation & Filter State ---
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedLevel = MutableStateFlow(AcademicLevel.SSC)
    val selectedLevel: StateFlow<AcademicLevel> = _selectedLevel.asStateFlow()

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Chapter?>(null)
    val selectedChapter: StateFlow<Chapter?> = _selectedChapter.asStateFlow()

    // 0: Audio Priming, 1: Concept & Mindmap, 2: Socratic Doubt, 3: Board Exam
    private val _selectedStepTab = MutableStateFlow(0)
    val selectedStepTab: StateFlow<Int> = _selectedStepTab.asStateFlow()

    // --- Audio Player State ---
    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _audioProgressSeconds = MutableStateFlow(0)
    val audioProgressSeconds: StateFlow<Int> = _audioProgressSeconds.asStateFlow()

    private var audioJob: Job? = null

    // --- Socratic Doubt Solver State ---
    private val _socraticMessages = MutableStateFlow<List<SocraticMessage>>(emptyList())
    val socraticMessages: StateFlow<List<SocraticMessage>> = _socraticMessages.asStateFlow()

    private val _isSocraticLoading = MutableStateFlow(false)
    val isSocraticLoading: StateFlow<Boolean> = _isSocraticLoading.asStateFlow()

    // --- Self-Test MCQ State ---
    private val _mcqSelectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val mcqSelectedAnswers: StateFlow<Map<Int, Int>> = _mcqSelectedAnswers.asStateFlow()

    private val _isMCQSubmitted = MutableStateFlow(false)
    val isMCQSubmitted: StateFlow<Boolean> = _isMCQSubmitted.asStateFlow()

    private val _mcqScore = MutableStateFlow(0)
    val mcqScore: StateFlow<Int> = _mcqScore.asStateFlow()

    // --- Creative Question (CQ) Answers & Evaluation ---
    private val _answerKa = MutableStateFlow("")
    val answerKa: StateFlow<String> = _answerKa.asStateFlow()

    private val _answerKha = MutableStateFlow("")
    val answerKha: StateFlow<String> = _answerKha.asStateFlow()

    private val _answerGa = MutableStateFlow("")
    val answerGa: StateFlow<String> = _answerGa.asStateFlow()

    private val _answerGha = MutableStateFlow("")
    val answerGha: StateFlow<String> = _answerGha.asStateFlow()

    private val _cqEvaluation = MutableStateFlow<CQEvaluationResult?>(null)
    val cqEvaluation: StateFlow<CQEvaluationResult?> = _cqEvaluation.asStateFlow()

    private val _isCQEvaluating = MutableStateFlow(false)
    val isCQEvaluating: StateFlow<Boolean> = _isCQEvaluating.asStateFlow()

    private val _isCQSavedToVault = MutableStateFlow(false)
    val isCQSavedToVault: StateFlow<Boolean> = _isCQSavedToVault.asStateFlow()

    // --- Persistence & 100:6:1 Funnel ---
    val allMistakes: StateFlow<List<MistakeVaultEntity>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val funnelStats: StateFlow<FunnelStats> = repository.funnelStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FunnelStats(0, 0, 0))

    // Navigation functions
    fun navigateToHome() {
        pauseAudio()
        _currentScreen.value = AppScreen.HOME
    }

    fun navigateToMistakeVault() {
        _currentScreen.value = AppScreen.MISTAKE_VAULT
    }

    fun selectLevel(level: AcademicLevel) {
        _selectedLevel.value = level
    }

    fun selectSubject(subject: Subject?) {
        _selectedSubject.value = subject
    }

    fun openChapter(chapter: Chapter) {
        _selectedChapter.value = chapter
        _selectedStepTab.value = 0
        _currentScreen.value = AppScreen.CHAPTER_DETAIL
        resetChapterState(chapter)
    }

    fun setStepTab(tabIndex: Int) {
        _selectedStepTab.value = tabIndex
        if (tabIndex != 0) {
            pauseAudio()
        }
        val chapter = _selectedChapter.value ?: return
        if (tabIndex == 1) {
            viewModelScope.launch {
                repository.updateChapterProgress(chapter.id, mindmapDone = true)
            }
        }
    }

    private fun resetChapterState(chapter: Chapter) {
        pauseAudio()
        _audioProgressSeconds.value = 0
        _mcqSelectedAnswers.value = emptyMap()
        _isMCQSubmitted.value = false
        _mcqScore.value = 0
        _answerKa.value = ""
        _answerKha.value = ""
        _answerGa.value = ""
        _answerGha.value = ""
        _cqEvaluation.value = null
        _isCQSavedToVault.value = false

        // Starter welcome message in Socratic Tutor
        _socraticMessages.value = listOf(
            SocraticMessage(
                id = "welcome",
                sender = SenderType.SOCRATIC_AI,
                text = "স্বাগতম! আমি গুগল লার্নএলএম পেডাগোজিক্যাল সলভার। '${chapter.titleBn}' অধ্যায়ের যেকোনো জটিল সূত্র বা কনসেপ্ট নিয়ে বাংলায় বা বাংলিশে প্রশ্ন করো। আমি সরাসরি উত্তর না দিয়ে ধাপে ধাপে তোমাকে সঠিক চিন্তায় পথ দেখাব।"
            )
        )
    }

    // Audio Player Controls
    fun togglePlayPauseAudio() {
        if (_isPlayingAudio.value) {
            pauseAudio()
        } else {
            playAudio()
        }
    }

    private fun playAudio() {
        val chapter = _selectedChapter.value ?: return
        val totalSeconds = chapter.podcast.episodeDurationSeconds
        // Bug fix: if playback reached the end, reset to 0 to replay cleanly
        if (_audioProgressSeconds.value >= totalSeconds) {
            _audioProgressSeconds.value = 0
        }
        _isPlayingAudio.value = true
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            while (_isPlayingAudio.value && _audioProgressSeconds.value < totalSeconds) {
                val delayTime = (1000 / _playbackSpeed.value).toLong()
                delay(delayTime)
                val nextSec = _audioProgressSeconds.value + 1
                _audioProgressSeconds.value = nextSec
                if (nextSec >= totalSeconds) {
                    _isPlayingAudio.value = false
                    // Mark audio completed in progress
                    repository.updateChapterProgress(chapter.id, audioDone = true)
                }
            }
        }
    }

    private fun pauseAudio() {
        _isPlayingAudio.value = false
        audioJob?.cancel()
    }

    fun seekAudio(targetSecond: Int) {
        val chapter = _selectedChapter.value ?: return
        _audioProgressSeconds.value = targetSecond.coerceIn(0, chapter.podcast.episodeDurationSeconds)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    // Socratic Chat
    fun sendSocraticDoubt(query: String) {
        if (query.isBlank()) return
        val chapter = _selectedChapter.value ?: return

        val userMsg = SocraticMessage(
            id = System.currentTimeMillis().toString(),
            sender = SenderType.STUDENT,
            text = query
        )
        _socraticMessages.value = _socraticMessages.value + userMsg

        viewModelScope.launch {
            _isSocraticLoading.value = true
            try {
                val response = GeminiApiClient.solveDoubtSocratically(query, chapter.titleBn)
                val aiMsg = SocraticMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    sender = SenderType.SOCRATIC_AI,
                    text = response.replyText,
                    banglishConverted = response.banglishConverted,
                    scaffoldingPrompt = response.scaffolding,
                    retentionScenario = response.retentionScenario
                )
                _socraticMessages.value = _socraticMessages.value + aiMsg
                repository.updateChapterProgress(chapter.id, doubtUsed = true)
            } finally {
                _isSocraticLoading.value = false
            }
        }
    }

    // MCQ Self-Test
    fun selectMCQAnswer(questionIndex: Int, optionIndex: Int) {
        if (_isMCQSubmitted.value) return
        _mcqSelectedAnswers.value = _mcqSelectedAnswers.value + (questionIndex to optionIndex)
    }

    fun submitMCQQuiz() {
        val chapter = _selectedChapter.value ?: return
        var correctCount = 0
        chapter.mcqs.forEachIndexed { index, mcq ->
            val selected = _mcqSelectedAnswers.value[index]
            if (selected == mcq.correctIndex) {
                correctCount++
            } else {
                // Automatically capture weak point into mistake vault
                viewModelScope.launch {
                    val wrongOpt = if (selected != null) mcq.options.getOrElse(selected) { "অপ্রদত্ত" } else "উত্তর দেওয়া হয়নি"
                    repository.addMistake(
                        chapterId = chapter.id,
                        chapterTitle = chapter.titleBn,
                        subjectName = chapter.subject.labelBn,
                        questionType = "MCQ",
                        questionSnippet = mcq.question,
                        studentAnswer = "প্রদত্ত উত্তর: $wrongOpt",
                        rubricFeedback = mcq.explanation,
                        recommendedKeyword = mcq.options[mcq.correctIndex]
                    )
                }
            }
        }
        _mcqScore.value = correctCount
        _isMCQSubmitted.value = true

        viewModelScope.launch {
            repository.updateChapterProgress(
                chapterId = chapter.id,
                mcqScore = correctCount,
                isMastered = correctCount >= 8 && (_cqEvaluation.value?.totalScore ?: 0) >= 8
            )
        }
    }

    fun resetMCQQuiz() {
        _mcqSelectedAnswers.value = emptyMap()
        _isMCQSubmitted.value = false
        _mcqScore.value = 0
    }

    // CQ Handlers
    fun updateAnswerKa(text: String) { _answerKa.value = text }
    fun updateAnswerKha(text: String) { _answerKha.value = text }
    fun updateAnswerGa(text: String) { _answerGa.value = text }
    fun updateAnswerGha(text: String) { _answerGha.value = text }

    fun evaluateCreativeQuestion() {
        val chapter = _selectedChapter.value ?: return
        viewModelScope.launch {
            _isCQEvaluating.value = true
            try {
                val result = GeminiApiClient.evaluateCQWithChiefExaminer(
                    cq = chapter.cq,
                    ansKa = _answerKa.value,
                    ansKha = _answerKha.value,
                    ansGa = _answerGa.value,
                    ansGha = _answerGha.value
                )
                _cqEvaluation.value = result

                // Update mastery status
                val isChapterMastered = (_mcqScore.value >= 8) && (result.totalScore >= 8)
                repository.updateChapterProgress(
                    chapterId = chapter.id,
                    cqScore = result.totalScore,
                    isMastered = isChapterMastered
                )
            } finally {
                _isCQEvaluating.value = false
            }
        }
    }

    fun flagCQToMistakeVault() {
        val chapter = _selectedChapter.value ?: return
        val eval = _cqEvaluation.value ?: return
        _isCQSavedToVault.value = true
        viewModelScope.launch {
            repository.addMistake(
                chapterId = chapter.id,
                chapterTitle = chapter.titleBn,
                subjectName = chapter.subject.labelBn,
                questionType = "CQ (সৃজনশীল প্রশ্ন)",
                questionSnippet = chapter.cq.questionKha + " | " + chapter.cq.questionGha,
                studentAnswer = "ক: ${_answerKa.value.take(40)}... ঘ: ${_answerGha.value.take(60)}...",
                rubricFeedback = eval.overallExaminerNote + "\n" + eval.feedbackGha,
                recommendedKeyword = eval.missingKeywords.joinToString(", ")
            )
        }
    }

    fun toggleMistakeResolved(item: MistakeVaultEntity) {
        viewModelScope.launch {
            repository.toggleMistakeResolved(item)
        }
    }

    fun deleteMistake(id: Long) {
        viewModelScope.launch {
            repository.deleteMistake(id)
        }
    }
}
