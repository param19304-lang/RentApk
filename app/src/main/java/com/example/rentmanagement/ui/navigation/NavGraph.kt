package com.example.rentmanagement.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rentmanagement.data.entities.UserAccountEntity
import com.example.rentmanagement.domain.model.UserRole
import com.example.rentmanagement.ui.auth.UserManagementScreen
import com.example.rentmanagement.ui.dashboard.DashboardScreen
import com.example.rentmanagement.ui.documents.DocumentsScreen
import com.example.rentmanagement.ui.expenses.AddEditExpenseScreen
import com.example.rentmanagement.ui.expenses.ExpensesScreen
import com.example.rentmanagement.ui.leases.AddLeaseScreen
import com.example.rentmanagement.ui.leases.LeaseDetailScreen
import com.example.rentmanagement.ui.leases.LeasesScreen
import com.example.rentmanagement.ui.payments.PaymentsScreen
import com.example.rentmanagement.ui.payments.RecordPaymentScreen
import com.example.rentmanagement.ui.properties.AddEditPropertyScreen
import com.example.rentmanagement.ui.properties.PropertiesScreen
import com.example.rentmanagement.ui.properties.PropertyDetailScreen
import com.example.rentmanagement.ui.rent.RentScreen
import com.example.rentmanagement.ui.reports.ReportsScreen
import com.example.rentmanagement.ui.settings.BackupRestoreScreen
import com.example.rentmanagement.ui.settings.DashboardCustomizationScreen
import com.example.rentmanagement.ui.settings.SettingsScreen
import com.example.rentmanagement.ui.tenants.AddEditTenantScreen
import com.example.rentmanagement.ui.tenants.TenantDetailScreen
import com.example.rentmanagement.ui.tenants.TenantsScreen
import com.example.rentmanagement.ui.units.UnitFormScreen
import kotlinx.coroutines.launch

private fun bottomIcon(route: String) = when (route) {
    Routes.DASHBOARD -> Icons.Default.Home
    Routes.PROPERTIES -> Icons.Default.Apartment
    Routes.RENT -> Icons.Default.Receipt
    Routes.PAYMENTS -> Icons.Default.Payments
    else -> Icons.Default.Home
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    currentUser: UserAccountEntity,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val isAdmin = currentUser.role == UserRole.ADMIN
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun navigateFromDrawer(route: String) {
        scope.launch { drawerState.close() }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentUser = currentUser,
                currentRoute = currentRoute,
                onNavigate = { route -> navigateFromDrawer(route) },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
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
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onAddProperty = { navController.navigate(Routes.propertyForm()) },
                        onAddTenant = { navController.navigate(Routes.tenantForm()) },
                        onRecordPayment = { navController.navigate(Routes.RENT) },
                        onAddExpense = { navController.navigate(Routes.expenseForm()) }
                    )
                }
                composable(Routes.PROPERTIES) {
                    PropertiesScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
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
                    RentScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onRecordPayment = { rentId -> navController.navigate(Routes.paymentRecord(rentId)) }
                    )
                }
                composable(Routes.PAYMENTS) {
                    PaymentsScreen(onMenuClick = { scope.launch { drawerState.open() } })
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
                composable(Routes.TENANTS) {
                    TenantsScreen(
                        onBack = { navController.popBackStack() },
                        onAddTenant = { navController.navigate(Routes.tenantForm()) },
                        onOpenTenant = { id -> navController.navigate(Routes.tenantDetail(id)) }
                    )
                }
                composable(
                    route = Routes.TENANT_DETAIL,
                    arguments = listOf(navArgument("tenantId") { type = NavType.LongType })
                ) { entry ->
                    val id = entry.arguments?.getLong("tenantId") ?: 0L
                    TenantDetailScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.tenantForm(id)) }
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
                        onAddLease = { navController.navigate(Routes.LEASE_FORM) },
                        onOpenLease = { id -> navController.navigate(Routes.leaseDetail(id)) }
                    )
                }
                composable(Routes.LEASE_FORM) {
                    AddLeaseScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.LEASE_DETAIL,
                    arguments = listOf(navArgument("leaseId") { type = NavType.LongType })
                ) {
                    LeaseDetailScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.EXPENSES) {
                    ExpensesScreen(
                        onBack = { navController.popBackStack() },
                        onAddExpense = { navController.navigate(Routes.expenseForm()) },
                        onOpenExpense = { id -> navController.navigate(Routes.expenseForm(id)) }
                    )
                }
                composable(
                    route = Routes.EXPENSE_FORM,
                    arguments = listOf(navArgument("expenseId") { type = NavType.LongType; defaultValue = -1L })
                ) { entry ->
                    val id = entry.arguments?.getLong("expenseId") ?: -1L
                    AddEditExpenseScreen(expenseId = if (id > 0) id else null, onBack = { navController.popBackStack() })
                }
                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        currentUserName = currentUser.fullName,
                        currentUserRole = currentUser.role.name,
                        isAdmin = isAdmin,
                        onManageUsers = { navController.navigate(Routes.USER_MANAGEMENT) },
                        onCustomizeDashboard = { navController.navigate(Routes.DASHBOARD_CUSTOMIZATION) },
                        onLogout = onLogout
                    )
                }
                composable(Routes.DASHBOARD_CUSTOMIZATION) {
                    DashboardCustomizationScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.REPORTS) {
                    ReportsScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.DOCUMENTS) {
                    DocumentsScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.BACKUP_RESTORE) {
                    BackupRestoreScreen(onBack = { navController.popBackStack() })
                }
                if (isAdmin) {
                    composable(Routes.USER_MANAGEMENT) {
                        UserManagementScreen(
                            onBack = { navController.popBackStack() },
                            currentUserId = currentUser.id
                        )
                    }
                }
            }
        }
    }
}
