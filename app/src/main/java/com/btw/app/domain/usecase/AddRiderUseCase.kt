package com.btw.app.domain.usecase

import com.btw.app.domain.model.Rider
import com.btw.app.domain.repository.RiderRepository
import javax.inject.Inject

class AddRiderUseCase @Inject constructor(
    private val repository: RiderRepository
) {
    suspend operator fun invoke(rider: Rider): Long = repository.addRider(rider)
}
