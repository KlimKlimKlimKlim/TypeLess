package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.data.repository.SnippetRepository
import kotlinx.coroutines.flow.first
import java.io.OutputStream
import javax.inject.Inject

class ExportSnippetsUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val premiumRepository: PremiumRepository
) {
    sealed class Result {
        object Success : Result()
        object PremiumRequired : Result()
    }

    suspend operator fun invoke(outputStream: OutputStream): Result {
        val isPremium = premiumRepository.isPremium.first()
        if (!isPremium) return Result.PremiumRequired

        repository.exportToStream(outputStream)
        return Result.Success
    }
}