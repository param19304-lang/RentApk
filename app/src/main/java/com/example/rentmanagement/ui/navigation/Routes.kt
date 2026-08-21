package com.example.rentmanagement.ui.navigation

object Routes {
    const val DASHBOARD = "dashboard"
    const val PROPERTIES = "properties"
    const val PROPERTY_FORM = "property_form?propertyId={propertyId}"
    fun propertyForm(propertyId: Long? = null) = "property_form?propertyId=${propertyId ?: -1}"
    const val PROPERTY_DETAIL = "property_detail/{propertyId}"
    fun propertyDetail(propertyId: Long) = "property_detail/$propertyId"

    const val UNIT_FORM = "unit_form?propertyId={propertyId}&unitId={unitId}"
    fun unitForm(propertyId: Long, unitId: Long? = null) = "unit_form?propertyId=$propertyId&unitId=${unitId ?: -1}"

    const val RENT = "rent"
    const val PAYMENTS = "payments"
    const val PAYMENT_RECORD = "payment_record/{rentId}"
    fun paymentRecord(rentId: Long) = "payment_record/$rentId"

    const val MORE = "more"
    const val TENANTS = "tenants"
    const val TENANT_FORM = "tenant_form?tenantId={tenantId}"
    fun tenantForm(tenantId: Long? = null) = "tenant_form?tenantId=${tenantId ?: -1}"

    const val LEASES = "leases"
    const val LEASE_FORM = "lease_form"

    const val EXPENSES = "expenses"
    const val REPORTS = "reports"
    const val REMINDERS = "reminders"
    const val DOCUMENTS = "documents"
    const val SETTINGS = "settings"
    const val BACKUP_RESTORE = "backup_restore"
}

data class BottomNavItem(val route: String, val label: String)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, "Dashboard"),
    BottomNavItem(Routes.PROPERTIES, "Properties"),
    BottomNavItem(Routes.RENT, "Rent"),
    BottomNavItem(Routes.PAYMENTS, "Payments"),
    BottomNavItem(Routes.MORE, "More")
)
