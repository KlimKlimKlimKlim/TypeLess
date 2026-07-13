package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.data.repository.UnlockRepository
import kotlinx.coroutines.flow.first
import java.io.OutputStream
import javax.inject.Inject

class ExportSnippetsUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val unlockRepository: UnlockRepository
) {
    sealed class Result {
        object Success : Result()
        object Restricted : Result()
        object Error : Result()
    }

    suspend operator fun invoke(outputStream: OutputStream): Result {
        val isUnlocked = unlockRepository.isUnlocked.first()
        if (!isUnlocked) return Result.Restricted

        return try {
            repository.exportToStream(outputStream)
            Result.Success
        } catch (_: Exception) {
            Result.Error
        }
    }
}