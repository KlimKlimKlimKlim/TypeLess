package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.model.Snippet
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject

class ImportSnippetsUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val premiumRepository: PremiumRepository
) {
    sealed class Result {
        object Success : Result()
        object Restricted : Result()
        object InvalidFile : Result()
        object Error : Result()
    }

    suspend operator fun invoke(inputStream: InputStream): Result {
        val hasPremiumAccess = premiumRepository.hasPremiumAccess.first()
        if (!hasPremiumAccess) return Result.Restricted

        return try {
            val json = inputStream.bufferedReader().use { it.readText() }
            val snippets = Json.decodeFromString<List<Snippet>>(json)
            snippets.forEach { repository.save(it.copy(id = 0)) }
            Result.Success
        } catch (e: kotlinx.serialization.SerializationException) {
            Result.InvalidFile
        } catch (_: Exception) {
            Result.Error
        }
    }
}