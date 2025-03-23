package com.cbo.memcloud.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create notebooks table
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notebooks (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    isDefault INTEGER NOT NULL
                )
                """
            )
            
            // Add notebookId column to notes table
            database.execSQL("ALTER TABLE notes ADD COLUMN notebookId TEXT NOT NULL DEFAULT 'default'")
            
            // Insert default notebook
            database.execSQL(
                """
                INSERT OR IGNORE INTO notebooks (id, name, description, createdAt, updatedAt, isDefault)
                VALUES ('default', 'Default', 'Default notebook', ${System.currentTimeMillis()}, ${System.currentTimeMillis()}, 1)
                """
            )
        }
    }
} 