package com.tonedefapps.btw.ui.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.data.billing.BillingManager
import com.tonedefapps.btw.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager,
    billingRepository: BillingRepository
) : ViewModel() {

    val isPremium: StateFlow<Boolean> = billingRepository.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun startPurchase(activity: Activity, productId: String) {
        _errorMessage.value = null
        billingManager.launchBillingFlow(activity, productId) { error ->
            _errorMessage.value = error
        }
    }

    fun restore() {
        viewModelScope.launch { billingManager.restorePurchases() }
    }
}
