package com.cbo.memcloud.feature.notes.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {
    // This module can be used to provide dependencies specific to the notes feature
    // Currently, we're using the repository from the core/data module
} 