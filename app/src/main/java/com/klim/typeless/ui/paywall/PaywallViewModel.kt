package com.klim.typeless.ui.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.data.repository.PremiumRepository
import com.klim.typeless.domain.usecase.UnlockForRewardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    premiumRepository: PremiumRepository,
    private val unlockForRewardUseCase: UnlockForRewardUseCase
) : ViewModel() {

    private val currentTimeFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1_000)
        }
    }

    val uiState: StateFlow<PaywallUiState> = combine(
        premiumRepository.unlockedUntil,
        currentTimeFlow
    ) { unlockedUntil, currentTime ->
        val remainingMillis = (unlockedUntil - currentTime).coerceAtLeast(0L)
        PaywallUiState(
            isUnlocked = remainingMillis > 0,
            unlockedUntil = unlockedUntil,
            remainingMillis = remainingMillis
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaywallUiState()
    )

    fun unlockForTesting() {
        viewModelScope.launch {
            unlockForRewardUseCase()
        }
    }
}

data class PaywallUiState(
    val isUnlocked: Boolean = false,
    val unlockedUntil: Long = 0L,
    val remainingMillis: Long = 0L
)