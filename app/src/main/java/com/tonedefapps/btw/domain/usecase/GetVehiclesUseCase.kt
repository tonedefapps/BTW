package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.Vehicle
import com.tonedefapps.btw.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> = repository.getVehicles()
}
