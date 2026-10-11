package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MistakeVaultEntity
import com.example.data.model.StudentProgressEntity

@Database(
    entities = [MistakeVaultEntity::class, StudentProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShikkhaDatabase : RoomDatabase() {
    abstract fun shikkhaDao(): ShikkhaDao

    companion object {
        @Volatile
        private var INSTANCE: ShikkhaDatabase? = null

        fun getDatabase(context: Context): ShikkhaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShikkhaDatabase::class.java,
                    "shikkha_database"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
