package com.btw.app.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btw.app.domain.model.Rider
import com.btw.app.domain.model.RiderType
import com.btw.app.domain.usecase.AddRiderUseCase
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
