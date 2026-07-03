package com.klim.typeless.di

import android.content.Context
import androidx.room.Room
import com.klim.typeless.data.db.AppDatabase
import com.klim.typeless.data.db.SnippetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "typeless.db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideSnippetDao(database: AppDatabase): SnippetDao =
        database.snippetDao()
}