package com.example.rentmanagement.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun currentBillingMonth(): String =
        SimpleDateFormat(Constants.BILLING_MONTH_PATTERN, Locale.US).format(Date())

    fun billingMonthOf(millis: Long): String =
        SimpleDateFormat(Constants.BILLING_MONTH_PATTERN, Locale.US).format(Date(millis))

    fun formatDate(millis: Long): String =
        SimpleDateFormat(Constants.DISPLAY_DATE_PATTERN, Locale.getDefault()).format(Date(millis))

    fun dueDateForMonth(billingMonth: String, dueDay: Int): Long {
        val parts = billingMonth.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, dueDay.coerceAtMost(lastDay))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun startOfMonth(billingMonth: String): Long {
        val parts = billingMonth.split("-")
        val cal = Calendar.getInstance()
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun endOfMonth(billingMonth: String): Long {
        val parts = billingMonth.split("-")
        val cal = Calendar.getInstance()
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1, 0, 0, 0)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.timeInMillis
    }

    fun addMonths(millis: Long, months: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.add(Calendar.MONTH, months)
        return cal.timeInMillis
    }

    fun daysBetween(fromMillis: Long, toMillis: Long): Long =
        (toMillis - fromMillis) / (24 * 60 * 60 * 1000)
}
