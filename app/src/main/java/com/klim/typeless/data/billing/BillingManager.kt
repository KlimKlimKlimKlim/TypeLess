package com.klim.typeless.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.klim.typeless.data.repository.PremiumRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.min

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val premiumRepository: PremiumRepository
) : PurchasesUpdatedListener {

    companion object {
        const val PRODUCT_ID = "premium_upgrade"
        private const val MAX_RETRIES = 4
        private const val BASE_RETRY_DELAY_MS = 1000L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var retryCount = 0
    private var isConnecting = false
    private val pendingCallbacks = mutableListOf<() -> Unit>()

    fun connect(onReady: () -> Unit) {
        if (billingClient.isReady) {
            onReady()
            return
        }
        pendingCallbacks.add(onReady)
        if (isConnecting) return
        startConnection()
    }

    private fun startConnection() {
        isConnecting = true
        _connectionState.value = ConnectionState.Connecting
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                isConnecting = false
                if (result.responseCode == BillingResponseCode.OK) {
                    retryCount = 0
                    _connectionState.value = ConnectionState.Connected
                    val callbacks = pendingCallbacks.toList()
                    pendingCallbacks.clear()
                    callbacks.forEach { it() }
                } else {
                    _connectionState.value = ConnectionState.Failed
                    _billingState.value = BillingState.Error(result.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnecting = false
                _connectionState.value = ConnectionState.Disconnected
                retryConnection()
            }
        })
    }

    private fun retryConnection() {
        if (retryCount >= MAX_RETRIES) {
            _connectionState.value = ConnectionState.Failed
            _billingState.value = BillingState.Error("Не удалось подключиться к Google Play")
            return
        }
        val delayMs = BASE_RETRY_DELAY_MS * (1L shl min(retryCount, 4))
        retryCount++
        scope.launch {
            delay(delayMs)
            startConnection()
        }
    }

    suspend fun queryProduct(): ProductDetails? = suspendCancellableCoroutine { cont ->
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        billingClient.queryProductDetailsAsync(params) { _, productDetailsList ->
            cont.resume(productDetailsList.firstOrNull())
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, params)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    acknowledgePurchase(purchase)
                }
            }
        } else if (result.responseCode == BillingResponseCode.USER_CANCELED) {
            _billingState.value = BillingState.Cancelled
        } else {
            _billingState.value = BillingState.Error(result.debugMessage)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.isAcknowledged) {
            scope.launch { premiumRepository.setPremium(true) }
            _billingState.value = BillingState.Purchased
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingResponseCode.OK) {
                scope.launch { premiumRepository.setPremium(true) }
                _billingState.value = BillingState.Purchased
            }
        }
    }

    suspend fun restorePurchases(): Unit = suspendCancellableCoroutine { cont ->
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { _, purchases ->
            val hasPremium = purchases.any { purchase ->
                purchase.products.contains(PRODUCT_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            scope.launch { premiumRepository.setPremium(hasPremium) }
            cont.resume(Unit)
        }
    }
}

sealed class BillingState {
    object Idle : BillingState()
    object Purchased : BillingState()
    object Cancelled : BillingState()
    data class Error(val message: String) : BillingState()
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    object Failed : ConnectionState()
}