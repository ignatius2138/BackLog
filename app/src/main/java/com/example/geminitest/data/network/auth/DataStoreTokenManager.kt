package com.example.geminitest.data.network.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.geminitest.data.auth.TokenPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

const val secureTokenPrefsFileName = "secure_token_prefs.pb"
@Singleton
class DataStoreTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenPrefsSerializer: SecureTokenPrefsSerializer
) : ITokenManager {

    private val dataStore: DataStore<TokenPrefs> by lazy {
        DataStoreFactory.create(
            serializer = tokenPrefsSerializer,
            produceFile = { context.dataStoreFile(secureTokenPrefsFileName) }
        )
    }

    override suspend fun getRefreshToken(): String? = dataStore.data.map { it.refreshToken }.firstOrNull()

    override suspend fun getExpiresAt(): Long = dataStore.data.map { it.expiresAt }.firstOrNull() ?: 0L

    override suspend fun saveTokens(
        access: String,
        refresh: String?,
        expiresAt: Long
    ) {
        dataStore.updateData { prefs ->
            prefs.toBuilder()
                .setAccessToken(access)
                .setRefreshToken(refresh)
                .setExpiresAt(expiresAt)
                .build()
        }
    }

    override suspend fun getAccessToken(): String? = dataStore.data.map { it.accessToken }.firstOrNull()

    override suspend fun clearTokens() {
        dataStore.updateData { prefs -> prefs.toBuilder().clear().build() }
    }
}