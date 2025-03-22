package com.cbo.memcloud.feature.notes;

import com.cbo.memcloud.core.data.repository.NotesRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class NoteEditorViewModel_Factory implements Factory<NoteEditorViewModel> {
  private final Provider<NotesRepository> notesRepositoryProvider;

  public NoteEditorViewModel_Factory(Provider<NotesRepository> notesRepositoryProvider) {
    this.notesRepositoryProvider = notesRepositoryProvider;
  }

  @Override
  public NoteEditorViewModel get() {
    return newInstance(notesRepositoryProvider.get());
  }

  public static NoteEditorViewModel_Factory create(
      Provider<NotesRepository> notesRepositoryProvider) {
    return new NoteEditorViewModel_Factory(notesRepositoryProvider);
  }

  public static NoteEditorViewModel newInstance(NotesRepository notesRepository) {
    return new NoteEditorViewModel(notesRepository);
  }
}
