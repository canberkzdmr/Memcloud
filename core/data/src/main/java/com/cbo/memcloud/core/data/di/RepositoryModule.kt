package com.cbo.memcloud.core.data.di

import com.cbo.memcloud.core.data.repository.DefaultNotebooksRepository
import com.cbo.memcloud.core.data.repository.DefaultNotesRepository
import com.cbo.memcloud.core.data.repository.NotebooksRepository
import com.cbo.memcloud.core.data.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepository: DefaultNotesRepository
    ): NotesRepository
    
    @Binds
    @Singleton
    abstract fun bindNotebooksRepository(
        notebooksRepository: DefaultNotebooksRepository
    ): NotebooksRepository
} 