package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.repository.RiderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRidersUseCase @Inject constructor(
    private val repository: RiderRepository
) {
    operator fun invoke(): Flow<List<Rider>> = repository.getRiders()
}
