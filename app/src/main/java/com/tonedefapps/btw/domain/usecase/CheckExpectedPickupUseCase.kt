package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.PickupWindow
import com.tonedefapps.btw.domain.repository.HandoffRepository
import java.util.Calendar
import javax.inject.Inject

class CheckExpectedPickupUseCase @Inject constructor(
    private val repository: HandoffRepository
) {
    suspend operator fun invoke(locationId: Long): List<PickupWindow> {
        val now = Calendar.getInstance()
        return repository.getActiveWindowsForLocation(locationId)
            .filter { it.isActiveNow(now) }
    }
}
