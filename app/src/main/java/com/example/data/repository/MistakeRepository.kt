package com.example.data.repository

import com.example.data.local.ShikkhaDao
import com.example.data.model.FunnelStats
import com.example.data.model.MistakeVaultEntity
import com.example.data.model.StudentProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MistakeRepository(private val dao: ShikkhaDao) {

    val allMistakes: Flow<List<MistakeVaultEntity>> = dao.getAllMistakes()
    val allProgress: Flow<List<StudentProgressEntity>> = dao.getAllChapterProgress()

    val funnelStats: Flow<FunnelStats> = combine(
        allMistakes,
        allProgress
    ) { mistakes, progressList ->
        val totalMCQAttempted = progressList.sumOf { if (it.mcqScore > 0) 10 else 0 }
        val totalCQAttempted = progressList.sumOf { if (it.cqScore > 0) 1 else 0 }
        val questionsPracticed = totalMCQAttempted + totalCQAttempted
        val weaknessesDiagnosed = mistakes.count { !it.isResolved }
        val chaptersMastered = progressList.count { it.isMastered }

        FunnelStats(
            questionsPracticed = questionsPracticed,
            weaknessesDiagnosed = weaknessesDiagnosed,
            chaptersMastered = chaptersMastered
        )
    }

    suspend fun addMistake(
        chapterId: String,
        chapterTitle: String,
        subjectName: String,
        questionType: String,
        questionSnippet: String,
        studentAnswer: String,
        rubricFeedback: String,
        recommendedKeyword: String
    ): Long {
        val entity = MistakeVaultEntity(
            chapterId = chapterId,
            chapterTitle = chapterTitle,
            subjectName = subjectName,
            questionType = questionType,
            questionSnippet = questionSnippet,
            studentAnswer = studentAnswer,
            rubricFeedback = rubricFeedback,
            recommendedNCTBKeyword = recommendedKeyword,
            isResolved = false
        )
        return dao.insertMistake(entity)
    }

    suspend fun toggleMistakeResolved(item: MistakeVaultEntity) {
        dao.updateMistake(item.copy(isResolved = !item.isResolved))
    }

    suspend fun deleteMistake(id: Long) {
        dao.deleteMistake(id)
    }

    suspend fun updateChapterProgress(
        chapterId: String,
        audioDone: Boolean? = null,
        mindmapDone: Boolean? = null,
        doubtUsed: Boolean? = null,
        mcqScore: Int? = null,
        cqScore: Int? = null,
        isMastered: Boolean? = null
    ) {
        val current = dao.getChapterProgressSync(chapterId)
        val updated = StudentProgressEntity(
            chapterId = chapterId,
            isAudioCompleted = audioDone ?: current?.isAudioCompleted ?: false,
            isMindmapCompleted = mindmapDone ?: current?.isMindmapCompleted ?: false,
            isDoubtSolverUsed = doubtUsed ?: current?.isDoubtSolverUsed ?: false,
            mcqScore = mcqScore ?: current?.mcqScore ?: 0,
            cqScore = cqScore ?: current?.cqScore ?: 0,
            isMastered = isMastered ?: current?.isMastered ?: false
        )
        dao.saveChapterProgress(updated)
    }
}
