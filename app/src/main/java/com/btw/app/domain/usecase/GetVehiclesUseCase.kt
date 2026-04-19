package com.btw.app.domain.usecase

import com.btw.app.domain.model.Vehicle
import com.btw.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> = repository.getVehicles()
}
