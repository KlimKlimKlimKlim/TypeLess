package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.UnlockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUnlockStatusUseCase @Inject constructor(
    private val unlockRepository: UnlockRepository
) {
    operator fun invoke(): Flow<Boolean> = unlockRepository.isUnlocked
}