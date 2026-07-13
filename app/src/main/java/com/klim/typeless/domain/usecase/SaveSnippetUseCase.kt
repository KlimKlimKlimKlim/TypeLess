package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.model.Snippet
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveSnippetUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val premiumRepository: PremiumRepository
) {
    sealed class Result {
        object Success : Result()
        object LimitReached : Result()
        object FolderRestricted : Result()
        object ArgumentsRestricted : Result()
    }

    suspend operator fun invoke(snippet: Snippet): Result {
        val hasPremiumAccess = premiumRepository.hasPremiumAccess.first()

        if (!hasPremiumAccess) {
            if (snippet.folder != "General") return Result.FolderRestricted
            if (snippet.arguments.isNotEmpty()) return Result.ArgumentsRestricted

            val currentCount = repository.getSnippetsCount()
            if (currentCount >= FREE_SNIPPETS_LIMIT) return Result.LimitReached
        }

        repository.save(snippet)
        return Result.Success
    }

    companion object {
        const val FREE_SNIPPETS_LIMIT = 5
    }
}