package com.tonedefapps.btw.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.LocationSource
import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.domain.repository.LocationRepository
import com.tonedefapps.btw.domain.usecase.GetLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    getLocationsUseCase: GetLocationsUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: StateFlow<List<SavedLocation>> = getLocationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addManualLocation(label: String, emoji: String, lat: Double, lng: Double, radiusMeters: Float) {
        viewModelScope.launch {
            locationRepository.addLocation(
                SavedLocation(
                    label = label,
                    emoji = emoji,
                    lat = lat,
                    lng = lng,
                    radiusMeters = radiusMeters,
                    source = LocationSource.MANUAL,
                    confidence = 1.0f
                )
            )
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch { locationRepository.deleteLocation(id) }
    }
}
