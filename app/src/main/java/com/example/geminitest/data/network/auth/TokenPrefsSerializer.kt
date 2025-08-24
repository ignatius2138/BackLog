package com.example.geminitest.data.network.auth

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.geminitest.data.auth.TokenPrefs
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SecureTokenPrefsSerializer @Inject constructor(
    private val cryptoManager: CryptoManager
) : Serializer<TokenPrefs> {
    override val defaultValue: TokenPrefs = TokenPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TokenPrefs {
        return try {
            val decrypted = cryptoManager.decrypt(input.readBytes())
            TokenPrefs.parseFrom(decrypted)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: TokenPrefs, output: OutputStream) {
        val encrypted = cryptoManager.encrypt(t.toByteArray())
        output.write(encrypted)
    }
}