package com.example.rentmanagement.domain.usecase

import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.utils.Constants
import com.example.rentmanagement.utils.DateUtils
import javax.inject.Inject

class CalculateLeaseStatusUseCase @Inject constructor() {
    operator fun invoke(lease: LeaseEntity, now: Long = System.currentTimeMillis()): LeaseStatus {
        if (lease.status == LeaseStatus.TERMINATED) return LeaseStatus.TERMINATED
        return when {
            now > lease.endDate -> LeaseStatus.EXPIRED
            DateUtils.daysBetween(now, lease.endDate) <= Constants.LEASE_EXPIRING_SOON_WINDOW_DAYS -> LeaseStatus.EXPIRING_SOON
            else -> LeaseStatus.ACTIVE
        }
    }
}
