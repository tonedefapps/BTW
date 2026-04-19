package com.btw.app.domain.usecase

import com.btw.app.domain.model.PickupWindow
import com.btw.app.domain.repository.HandoffRepository
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
