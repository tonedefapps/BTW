package com.tonedefapps.btw.data.billing

import com.tonedefapps.btw.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingManager: BillingManager
) : BillingRepository {
    override val isPremium: Flow<Boolean> = billingManager.isPremium
    override suspend fun restorePurchases() = billingManager.restorePurchases()
}
