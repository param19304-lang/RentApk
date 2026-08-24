package com.example.rentmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rentmanagement.domain.model.LeaseStatus
import com.example.rentmanagement.domain.model.PaymentStatus
import com.example.rentmanagement.domain.model.UnitStatus
import com.example.rentmanagement.ui.theme.LocalSemanticColors

enum class BadgeTone { SUCCESS, WARNING, ERROR, INFO, NEUTRAL }

@Composable
fun StatusBadge(text: String, tone: BadgeTone, modifier: Modifier = Modifier) {
    val semantic = LocalSemanticColors.current
    val (bg, fg) = when (tone) {
        BadgeTone.SUCCESS -> semantic.success.copy(alpha = 0.16f) to semantic.success
        BadgeTone.WARNING -> semantic.warning.copy(alpha = 0.18f) to semantic.warning
        BadgeTone.ERROR -> semantic.error.copy(alpha = 0.14f) to semantic.error
        BadgeTone.INFO -> semantic.info.copy(alpha = 0.14f) to semantic.info
        BadgeTone.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = text,
        color = fg,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private fun PaymentStatus.toBadgeTone(): BadgeTone = when (this) {
    PaymentStatus.PAID -> BadgeTone.SUCCESS
    PaymentStatus.PARTIALLY_PAID -> BadgeTone.WARNING
    PaymentStatus.PENDING -> BadgeTone.INFO
    PaymentStatus.OVERDUE -> BadgeTone.ERROR
}

private fun LeaseStatus.toBadgeTone(): BadgeTone = when (this) {
    LeaseStatus.ACTIVE -> BadgeTone.SUCCESS
    LeaseStatus.EXPIRING_SOON -> BadgeTone.WARNING
    LeaseStatus.EXPIRED -> BadgeTone.ERROR
    LeaseStatus.TERMINATED -> BadgeTone.NEUTRAL
}

private fun UnitStatus.toBadgeTone(): BadgeTone = when (this) {
    UnitStatus.OCCUPIED -> BadgeTone.SUCCESS
    UnitStatus.VACANT -> BadgeTone.NEUTRAL
    UnitStatus.RESERVED -> BadgeTone.INFO
    UnitStatus.UNDER_MAINTENANCE -> BadgeTone.WARNING
}

private fun statusLabel(name: String) = name.replace('_', ' ')

@Composable
fun StatusBadge(status: PaymentStatus, modifier: Modifier = Modifier) =
    StatusBadge(text = statusLabel(status.name), tone = status.toBadgeTone(), modifier = modifier)

@Composable
fun StatusBadge(status: LeaseStatus, modifier: Modifier = Modifier) =
    StatusBadge(text = statusLabel(status.name), tone = status.toBadgeTone(), modifier = modifier)

@Composable
fun StatusBadge(status: UnitStatus, modifier: Modifier = Modifier) =
    StatusBadge(text = statusLabel(status.name), tone = status.toBadgeTone(), modifier = modifier)
