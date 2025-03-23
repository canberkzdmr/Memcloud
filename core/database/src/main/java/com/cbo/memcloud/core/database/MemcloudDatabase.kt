package com.cbo.memcloud.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cbo.memcloud.core.database.dao.NoteDao
import com.cbo.memcloud.core.database.dao.NotebookDao
import com.cbo.memcloud.core.database.model.NoteEntity
import com.cbo.memcloud.core.database.model.NotebookEntity
import com.cbo.memcloud.core.model.Notebook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NoteEntity::class,
        NotebookEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MemcloudDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun notebookDao(): NotebookDao

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
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Create default notebook when database is created
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).notebookDao().insertNotebook(
                                NotebookEntity.fromNotebook(Notebook.createDefault())
                            )
                        }
                    }
                })
                .addMigrations(
                    DatabaseMigrations.MIGRATION_1_2
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 