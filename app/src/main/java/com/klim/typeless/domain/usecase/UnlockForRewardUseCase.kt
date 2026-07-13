package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.PremiumRepository
import javax.inject.Inject

class UnlockForRewardUseCase @Inject constructor(
    private val premiumRepository: PremiumRepository
) {
    suspend operator fun invoke(hours: Int = 3) {
        premiumRepository.unlockForHours(hours)
    }
}