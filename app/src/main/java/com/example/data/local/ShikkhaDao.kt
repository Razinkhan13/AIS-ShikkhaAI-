package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MistakeVaultEntity
import com.example.data.model.StudentProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShikkhaDao {
    @Query("SELECT * FROM mistake_vault ORDER BY timestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeVaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(item: MistakeVaultEntity): Long

    @Update
    suspend fun updateMistake(item: MistakeVaultEntity)

    @Query("DELETE FROM mistake_vault WHERE id = :id")
    suspend fun deleteMistake(id: Long)

    @Query("SELECT COUNT(*) FROM mistake_vault WHERE isResolved = 0")
    fun getUnresolvedMistakeCount(): Flow<Int>

    @Query("SELECT * FROM student_progress WHERE chapterId = :chapterId")
    fun getChapterProgress(chapterId: String): Flow<StudentProgressEntity?>

    @Query("SELECT * FROM student_progress WHERE chapterId = :chapterId")
    suspend fun getChapterProgressSync(chapterId: String): StudentProgressEntity?

    @Query("SELECT * FROM student_progress")
    fun getAllChapterProgress(): Flow<List<StudentProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChapterProgress(progress: StudentProgressEntity)

    @Query("SELECT COUNT(*) FROM student_progress WHERE isMastered = 1")
    fun getMasteredChaptersCount(): Flow<Int>
}
