package com.tonedefapps.btw.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.tonedefapps.btw.data.preferences.BtwPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: BtwPreferences
) : PurchasesUpdatedListener {

    companion object {
        const val PRODUCT_MONTHLY  = "btw_premium_monthly"   // $0.99/mo sub
        const val PRODUCT_YEARLY   = "btw_premium_yearly"    // $9.99/yr sub
        const val PRODUCT_LIFETIME = "btw_premium_lifetime"  // $14.99 one-time
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val isPremium: Flow<Boolean> = preferences.alertPreferences.map { it.isPremium }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    init { connect() }

    private fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch { restorePurchases() }
                }
            }
            override fun onBillingServiceDisconnected() { connect() }
        })
    }

    suspend fun restorePurchases() {
        val hasActiveSub = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ).purchasesList.any { p ->
            (p.products.contains(PRODUCT_MONTHLY) || p.products.contains(PRODUCT_YEARLY)) &&
            p.purchaseState == Purchase.PurchaseState.PURCHASED
        }

        val hasLifetime = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ).purchasesList.any { p ->
            p.products.contains(PRODUCT_LIFETIME) &&
            p.purchaseState == Purchase.PurchaseState.PURCHASED
        }

        preferences.setPremium(hasActiveSub || hasLifetime)
    }

    fun launchBillingFlow(activity: Activity, productId: String, onError: (String) -> Unit) {
        if (!billingClient.isReady) {
            onError("billing unavailable — try again in a moment")
            return
        }
        val isLifetime = productId == PRODUCT_LIFETIME
        scope.launch(Dispatchers.Main) {
            val productType = if (isLifetime) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build()
            )
            val detailsResult = billingClient.queryProductDetails(
                QueryProductDetailsParams.newBuilder().setProductList(productList).build()
            )
            val product = detailsResult.productDetailsList?.firstOrNull() ?: run {
                onError("could not load product details")
                return@launch
            }
            val paramsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
            if (!isLifetime) {
                val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: run {
                    onError("no offer available")
                    return@launch
                }
                paramsBuilder.setOfferToken(offerToken)
            }
            billingClient.launchBillingFlow(
                activity,
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(paramsBuilder.build()))
                    .build()
            )
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch {
                for (purchase in purchases) {
                    val isPremiumProduct = purchase.products.any { id ->
                        id == PRODUCT_MONTHLY || id == PRODUCT_YEARLY || id == PRODUCT_LIFETIME
                    }
                    if (isPremiumProduct && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (!purchase.isAcknowledged) {
                            billingClient.acknowledgePurchase(
                                AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken)
                                    .build()
                            )
                        }
                        preferences.setPremium(true)
                    }
                }
            }
        }
    }
}
