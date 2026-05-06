package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.repository.RiderRepository
import javax.inject.Inject

class AddRiderUseCase @Inject constructor(
    private val repository: RiderRepository
) {
    suspend operator fun invoke(rider: Rider): Long = repository.addRider(rider)
}
