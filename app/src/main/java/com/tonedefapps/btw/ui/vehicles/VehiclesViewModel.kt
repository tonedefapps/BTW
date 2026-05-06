package com.tonedefapps.btw.ui.vehicles

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.Vehicle
import com.tonedefapps.btw.domain.repository.VehicleRepository
import com.tonedefapps.btw.domain.usecase.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    getVehiclesUseCase: GetVehiclesUseCase,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = getVehiclesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDevicePaired(device: BluetoothDevice) {
        viewModelScope.launch {
            val name = try { device.name ?: device.address } catch (_: SecurityException) { device.address }
            vehicleRepository.addVehicle(Vehicle(name = name, bluetoothAddress = device.address, isPrimary = true))
        }
    }

    fun onPairingFailed() { /* no-op — UI handles empty state */ }

    fun deleteVehicle(id: Long) {
        viewModelScope.launch { vehicleRepository.deleteVehicle(id) }
    }
}
