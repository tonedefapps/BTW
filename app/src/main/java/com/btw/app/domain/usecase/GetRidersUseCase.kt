package com.btw.app.domain.usecase

import com.btw.app.domain.model.Rider
import com.btw.app.domain.repository.RiderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRidersUseCase @Inject constructor(
    private val repository: RiderRepository
) {
    operator fun invoke(): Flow<List<Rider>> = repository.getRiders()
}
