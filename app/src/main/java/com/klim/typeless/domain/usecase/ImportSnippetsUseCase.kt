package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.data.repository.SnippetRepository
import kotlinx.coroutines.flow.first
import java.io.InputStream
import javax.inject.Inject

class ImportSnippetsUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val premiumRepository: PremiumRepository
) {
    sealed class Result {
        data class Success(val imported: Int) : Result()
        data class PartialSuccess(val imported: Int, val skipped: Int) : Result()
        object LimitReached : Result()
    }

    suspend operator fun invoke(inputStream: InputStream): Result {
        val isPremium = premiumRepository.isPremium.first()
        val incoming = parseStream(inputStream) ?: return Result.LimitReached

        if (isPremium) {
            var imported = 0
            for (snippet in incoming) {
                if (repository.findByTrigger(snippet.trigger) == null) {
                    repository.save(snippet.copy(id = 0))
                    imported++
                }
            }
            return Result.Success(imported)
        }

        val currentCount = repository.getSnippetsCount()
        val available = SaveSnippetUseCase.FREE_SNIPPETS_LIMIT - currentCount

        if (available <= 0) return Result.LimitReached

        var imported = 0
        var skipped = 0

        for (snippet in incoming) {
            if (imported >= available) {
                skipped++
                continue
            }
            if (snippet.folder != "General" || snippet.arguments.isNotEmpty()) {
                skipped++
                continue
            }
            if (repository.findByTrigger(snippet.trigger) == null) {
                repository.save(snippet.copy(id = 0, folder = "General", arguments = emptyList()))
                imported++
            } else {
                skipped++
            }
        }

        return if (skipped > 0) Result.PartialSuccess(imported, skipped) else Result.Success(imported)
    }

    private fun parseStream(inputStream: InputStream): List<com.klim.typeless.domain.model.Snippet>? =
        runCatching {
            kotlinx.serialization.json.Json.decodeFromString<List<com.klim.typeless.domain.model.Snippet>>(
                inputStream.use { it.readBytes().decodeToString() }
            )
        }.getOrNull()
}