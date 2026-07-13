package com.klim.typeless.domain.usecase

import android.net.Uri
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
        object Restricted : Result()
        object Error : Result()
    }

    suspend operator fun invoke(outputStream: OutputStream): Result {
        val hasPremiumAccess = premiumRepository.hasPremiumAccess.first()
        if (!hasPremiumAccess) return Result.Restricted

        return try {
            repository.exportToStream(outputStream)
            Result.Success
        } catch (_: Exception) {
            Result.Error
        }
    }
}