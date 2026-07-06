package com.klim.typeless.ui.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.data.billing.BillingManager
import com.klim.typeless.data.billing.BillingState
import com.klim.typeless.data.billing.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    val billingState: StateFlow<BillingState> = billingManager.billingState
    val connectionState: StateFlow<ConnectionState> = billingManager.connectionState

    fun startPurchase(activity: Activity) {
        billingManager.connect {
            viewModelScope.launch {
                val product = billingManager.queryProduct()
                if (product == null) {
                    return@launch
                }
                billingManager.launchBillingFlow(activity, product)
            }
        }
    }

    fun restorePurchases() {
        billingManager.connect {
            viewModelScope.launch {
                billingManager.restorePurchases()
            }
        }
    }
}