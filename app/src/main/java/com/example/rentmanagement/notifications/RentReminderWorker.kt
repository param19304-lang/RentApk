package com.example.rentmanagement.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rentmanagement.data.preferences.AppPreferences
import com.example.rentmanagement.data.repository.LeaseRepository
import com.example.rentmanagement.data.repository.RentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Daily background check (spec section 15): marks overdue rent, then notifies
 * on rent due soon, rent overdue, and leases expiring soon. One summary
 * notification per category rather than one per record, to avoid spamming.
 */
@HiltWorker
class RentReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val rentRepository: RentRepository,
    private val leaseRepository: LeaseRepository,
    private val appPreferences: AppPreferences
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val notificationsEnabled = appPreferences.notificationsEnabled.first()
        if (!notificationsEnabled) return Result.success()

        val now = System.currentTimeMillis()
        rentRepository.markOverdue(now)

        val reminderDays = appPreferences.reminderDaysBefore.first()
        val windowEnd = now + reminderDays.toLong() * 24 * 60 * 60 * 1000

        val dueSoon = rentRepository.getUpcomingDue(now, windowEnd).first()
        if (dueSoon.isNotEmpty()) {
            NotificationHelper.notify(
                applicationContext, 1001, "Rent due soon",
                "${dueSoon.size} payment(s) due in the next $reminderDays day(s)"
            )
        }

        val overdue = rentRepository.getOverdueRent().first()
        if (overdue.isNotEmpty()) {
            NotificationHelper.notify(
                applicationContext, 1002, "Rent overdue",
                "${overdue.size} payment(s) are overdue"
            )
        }

        val expiringLeases = leaseRepository.getLeasesExpiringBetween(now, windowEnd).first()
        if (expiringLeases.isNotEmpty()) {
            NotificationHelper.notify(
                applicationContext, 1003, "Lease expiring soon",
                "${expiringLeases.size} lease(s) expiring in the next $reminderDays day(s)"
            )
        }

        return Result.success()
    }
}
