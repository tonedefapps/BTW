package com.tonedefapps.btw.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderType
import com.tonedefapps.btw.domain.usecase.AddRiderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRiderViewModel @Inject constructor(
    private val addRiderUseCase: AddRiderUseCase
) : ViewModel() {

    fun addRider(name: String, type: RiderType, emoji: String) {
        viewModelScope.launch {
            addRiderUseCase(Rider(name = name, type = type, emoji = emoji))
        }
    }
}
