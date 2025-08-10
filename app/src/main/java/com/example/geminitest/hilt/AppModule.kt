package com.example.geminitest.hilt

import android.content.Context
import com.example.geminitest.data.database.GameDao
import com.example.geminitest.data.database.GameDatabase
import com.example.geminitest.data.database.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase {
        return GameDatabase.getDatabase(context)
    }

    @Provides
    fun provideGameDao(db: GameDatabase): GameDao {
        return db.gameDao()
    }

    @Provides
    fun provideGameRepository(dao: GameDao): GameRepository {
        return GameRepository(dao)
    }
}