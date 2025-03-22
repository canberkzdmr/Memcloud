package com.cbo.memcloud.core.data.repository;

import com.cbo.memcloud.core.database.dao.NoteDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DefaultNotesRepository_Factory implements Factory<DefaultNotesRepository> {
  private final Provider<NoteDao> noteDaoProvider;

  public DefaultNotesRepository_Factory(Provider<NoteDao> noteDaoProvider) {
    this.noteDaoProvider = noteDaoProvider;
  }

  @Override
  public DefaultNotesRepository get() {
    return newInstance(noteDaoProvider.get());
  }

  public static DefaultNotesRepository_Factory create(Provider<NoteDao> noteDaoProvider) {
    return new DefaultNotesRepository_Factory(noteDaoProvider);
  }

  public static DefaultNotesRepository newInstance(NoteDao noteDao) {
    return new DefaultNotesRepository(noteDao);
  }
}
