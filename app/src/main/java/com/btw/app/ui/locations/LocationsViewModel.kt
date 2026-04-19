package com.btw.app.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btw.app.domain.model.LocationSource
import com.btw.app.domain.model.SavedLocation
import com.btw.app.domain.repository.LocationRepository
import com.btw.app.domain.usecase.GetLocationsUseCase
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
