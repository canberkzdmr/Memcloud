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
public final class NotesViewModel_Factory implements Factory<NotesViewModel> {
  private final Provider<NotesRepository> notesRepositoryProvider;

  public NotesViewModel_Factory(Provider<NotesRepository> notesRepositoryProvider) {
    this.notesRepositoryProvider = notesRepositoryProvider;
  }

  @Override
  public NotesViewModel get() {
    return newInstance(notesRepositoryProvider.get());
  }

  public static NotesViewModel_Factory create(Provider<NotesRepository> notesRepositoryProvider) {
    return new NotesViewModel_Factory(notesRepositoryProvider);
  }

  public static NotesViewModel newInstance(NotesRepository notesRepository) {
    return new NotesViewModel(notesRepository);
  }
}
