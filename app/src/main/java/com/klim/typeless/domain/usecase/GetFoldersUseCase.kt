package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.data.repository.UnlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val unlockRepository: UnlockRepository
) {
    operator fun invoke(): Flow<List<String>> =
        combine(
            repository.getAllFolders(),
            unlockRepository.isUnlocked
        ) { folders, isUnlocked ->
            if (isUnlocked) {
                folders
            } else {
                folders.filter { it == "General" }
            }
        }
}