package com.cbo.memcloud.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cbo.memcloud.core.database.dao.NoteDao
import com.cbo.memcloud.core.database.model.NoteEntity

@Database(
    entities = [
        NoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MemcloudDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "memcloud-db"

        @Volatile
        private var INSTANCE: MemcloudDatabase? = null

        fun getInstance(context: Context): MemcloudDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemcloudDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 