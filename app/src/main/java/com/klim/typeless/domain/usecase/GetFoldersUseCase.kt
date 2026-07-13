package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.data.repository.SnippetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: SnippetRepository,
    private val premiumRepository: PremiumRepository
) {
    operator fun invoke(): Flow<List<String>> =
        combine(
            repository.getAllFolders(),
            premiumRepository.hasPremiumAccess
        ) { folders, hasPremiumAccess ->
            if (hasPremiumAccess) {
                folders
            } else {
                folders.filter { it == "General" }
            }
        }
}