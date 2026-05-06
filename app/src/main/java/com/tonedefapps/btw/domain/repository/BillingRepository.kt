package com.tonedefapps.btw.domain.repository

import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val isPremium: Flow<Boolean>
    suspend fun restorePurchases()
}
