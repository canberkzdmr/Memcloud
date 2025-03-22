package com.cbo.memcloud.core.database.di;

import android.content.Context;
import com.cbo.memcloud.core.database.MemcloudDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideMemcloudDatabaseFactory implements Factory<MemcloudDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideMemcloudDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MemcloudDatabase get() {
    return provideMemcloudDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideMemcloudDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideMemcloudDatabaseFactory(contextProvider);
  }

  public static MemcloudDatabase provideMemcloudDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMemcloudDatabase(context));
  }
}
