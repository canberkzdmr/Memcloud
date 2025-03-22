package com.cbo.memcloud.core.database.di

import android.content.Context
import com.cbo.memcloud.core.database.MemcloudDatabase
import com.cbo.memcloud.core.database.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMemcloudDatabase(@ApplicationContext context: Context): MemcloudDatabase {
        return MemcloudDatabase.getInstance(context)
    }

    @Provides
    fun provideNoteDao(database: MemcloudDatabase): NoteDao {
        return database.noteDao()
    }
} 