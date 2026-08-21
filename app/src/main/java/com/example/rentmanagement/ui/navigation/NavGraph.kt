package com.example.rentmanagement.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentmanagement.ui.dashboard.DashboardScreen
import com.example.rentmanagement.ui.leases.AddLeaseScreen
import com.example.rentmanagement.ui.leases.LeasesScreen
import com.example.rentmanagement.ui.more.MoreScreen
import com.example.rentmanagement.ui.payments.PaymentsScreen
import com.example.rentmanagement.ui.payments.RecordPaymentScreen
import com.example.rentmanagement.ui.properties.AddEditPropertyScreen
import com.example.rentmanagement.ui.properties.PropertiesScreen
import com.example.rentmanagement.ui.properties.PropertyDetailScreen
import com.example.rentmanagement.ui.rent.RentScreen
import com.example.rentmanagement.ui.settings.SettingsScreen
import com.example.rentmanagement.ui.tenants.AddEditTenantScreen
import com.example.rentmanagement.ui.tenants.TenantsScreen
import com.example.rentmanagement.ui.units.UnitFormScreen

private fun bottomIcon(route: String) = when (route) {
    Routes.DASHBOARD -> Icons.Default.Home
    Routes.PROPERTIES -> Icons.Default.List
    Routes.RENT -> Icons.Default.Receipt
    Routes.PAYMENTS -> Icons.Default.Payments
    else -> Icons.Default.MoreHoriz
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(bottomIcon(item.route), contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onAddProperty = { navController.navigate(Routes.propertyForm()) },
                    onAddTenant = { navController.navigate(Routes.tenantForm()) },
                    onRecordPayment = { navController.navigate(Routes.RENT) },
                    onAddExpense = { navController.navigate(Routes.MORE) }
                )
            }
            composable(Routes.PROPERTIES) {
                PropertiesScreen(
                    onAddProperty = { navController.navigate(Routes.propertyForm()) },
                    onOpenProperty = { id -> navController.navigate(Routes.propertyDetail(id)) }
                )
            }
            composable(
                route = Routes.PROPERTY_FORM,
                arguments = listOf(navArgument("propertyId") { type = NavType.LongType; defaultValue = -1L })
            ) { entry ->
                val id = entry.arguments?.getLong("propertyId") ?: -1L
                AddEditPropertyScreen(propertyId = if (id > 0) id else null, onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.PROPERTY_DETAIL,
                arguments = listOf(navArgument("propertyId") { type = NavType.LongType })
            ) { entry ->
                val propertyId = entry.arguments?.getLong("propertyId") ?: 0L
                PropertyDetailScreen(
                    propertyId = propertyId,
                    onBack = { navController.popBackStack() },
                    onAddUnit = { navController.navigate(Routes.unitForm(propertyId)) },
                    onEditUnit = { unitId -> navController.navigate(Routes.unitForm(propertyId, unitId)) }
                )
            }
            composable(
                route = Routes.UNIT_FORM,
                arguments = listOf(
                    navArgument("propertyId") { type = NavType.LongType },
                    navArgument("unitId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { entry ->
                val propertyId = entry.arguments?.getLong("propertyId") ?: 0L
                val unitId = entry.arguments?.getLong("unitId") ?: -1L
                UnitFormScreen(
                    propertyId = propertyId,
                    unitId = if (unitId > 0) unitId else null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.RENT) {
                RentScreen(onRecordPayment = { rentId -> navController.navigate(Routes.paymentRecord(rentId)) })
            }
            composable(Routes.PAYMENTS) {
                PaymentsScreen()
            }
            composable(
                route = Routes.PAYMENT_RECORD,
                arguments = listOf(navArgument("rentId") { type = NavType.LongType })
            ) { entry ->
                val rentId = entry.arguments?.getLong("rentId") ?: 0L
                RecordPaymentScreen(
                    rentId = rentId,
                    onBack = { navController.popBackStack() },
                    onRecorded = { navController.popBackStack() }
                )
            }
            composable(Routes.MORE) {
                MoreScreen(
                    onTenants = { navController.navigate(Routes.TENANTS) },
                    onExpenses = { /* Phase 2 */ },
                    onLeases = { navController.navigate(Routes.LEASES) },
                    onReports = { /* Phase 2 */ },
                    onReminders = { /* Phase 2 */ },
                    onDocuments = { /* Phase 2 */ },
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                    onBackupRestore = { /* Phase 2 */ }
                )
            }
            composable(Routes.TENANTS) {
                TenantsScreen(
                    onBack = { navController.popBackStack() },
                    onAddTenant = { navController.navigate(Routes.tenantForm()) },
                    onOpenTenant = { id -> navController.navigate(Routes.tenantForm(id)) }
                )
            }
            composable(
                route = Routes.TENANT_FORM,
                arguments = listOf(navArgument("tenantId") { type = NavType.LongType; defaultValue = -1L })
            ) { entry ->
                val id = entry.arguments?.getLong("tenantId") ?: -1L
                AddEditTenantScreen(tenantId = if (id > 0) id else null, onBack = { navController.popBackStack() })
            }
            composable(Routes.LEASES) {
                LeasesScreen(
                    onBack = { navController.popBackStack() },
                    onAddLease = { navController.navigate(Routes.LEASE_FORM) }
                )
            }
            composable(Routes.LEASE_FORM) {
                AddLeaseScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
