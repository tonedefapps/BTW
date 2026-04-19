package com.btw.app.ui.setup

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btw.app.domain.model.Vehicle
import com.btw.app.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PairVehicleUiState(
    val pairedDeviceName: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class PairVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PairVehicleUiState())
    val uiState: StateFlow<PairVehicleUiState> = _uiState.asStateFlow()

    fun onDevicePaired(device: BluetoothDevice) {
        viewModelScope.launch {
            val name = try { device.name ?: device.address } catch (_: SecurityException) { device.address }
            val vehicle = Vehicle(
                name = name,
                bluetoothAddress = device.address,
                isPrimary = true
            )
            vehicleRepository.addVehicle(vehicle)
            _uiState.value = _uiState.value.copy(pairedDeviceName = name)
        }
    }

    fun onPairingFailed() {
        _uiState.value = _uiState.value.copy(error = "couldn't find a device. try again.")
    }
}
