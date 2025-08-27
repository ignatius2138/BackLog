package com.example.geminitest.hilt

import android.content.Context
import com.example.geminitest.BuildConfig
import com.example.geminitest.data.network.auth.DataStoreTokenManager
import com.example.geminitest.data.network.auth.ITokenManager
import com.example.geminitest.data.network.auth.TwitchAuthManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideTwitchAuthManager(
        @ApplicationContext context: Context,
        tokenManager: ITokenManager
    ): TwitchAuthManager {
        return TwitchAuthManager(
            context = context,
            clientId = BuildConfig.IGDB_CLIENT_ID,
            redirectUri = "com.example.geminitest://oauth2/callback",
            tokenManager = tokenManager
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {

    @Binds
    @Singleton
    abstract fun bindTokenManager(
        dataStoreTokenManager: DataStoreTokenManager
    ): ITokenManager
}