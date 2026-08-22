package com.example.rentmanagement.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rentmanagement.data.entities.UserAccountEntity
import com.example.rentmanagement.domain.model.UserRole

private data class DrawerItem(val label: String, val icon: ImageVector, val route: String)

private val primaryDrawerItems = listOf(
    DrawerItem("Dashboard", Icons.Default.Home, Routes.DASHBOARD),
    DrawerItem("Properties", Icons.Default.Apartment, Routes.PROPERTIES),
    DrawerItem("Rent", Icons.Default.Receipt, Routes.RENT),
    DrawerItem("Payments", Icons.Default.Payments, Routes.PAYMENTS)
)

private val managementDrawerItems = listOf(
    DrawerItem("Tenants", Icons.Default.People, Routes.TENANTS),
    DrawerItem("Leases", Icons.Default.Description, Routes.LEASES),
    DrawerItem("Expenses", Icons.Default.AttachMoney, Routes.EXPENSES),
    DrawerItem("Reports", Icons.Default.Assessment, Routes.REPORTS),
    DrawerItem("Documents", Icons.Default.Folder, Routes.DOCUMENTS)
)

/**
 * Full app navigation menu, opened from the hamburger icon on the four
 * primary screens. Replaces the old bottom-nav "More" tab — everything lives
 * here now, plus admin-only Users management and Logout.
 */
@Composable
fun AppDrawerContent(
    currentUser: UserAccountEntity,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val isAdmin = currentUser.role == UserRole.ADMIN

    ModalDrawerSheet {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text("Rent Manager", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(currentUser.fullName, style = MaterialTheme.typography.bodyMedium)
            Text(
                currentUser.role.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider()

        Spacer(Modifier.height(8.dp))
        primaryDrawerItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        managementDrawerItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        if (isAdmin) {
            NavigationDrawerItem(
                label = { Text("Users") },
                icon = { Icon(Icons.Default.Group, contentDescription = null) },
                selected = currentRoute == Routes.USER_MANAGEMENT,
                onClick = { onNavigate(Routes.USER_MANAGEMENT) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            selected = currentRoute == Routes.SETTINGS,
            onClick = { onNavigate(Routes.SETTINGS) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Backup & Restore") },
            icon = { Icon(Icons.Default.Backup, contentDescription = null) },
            selected = currentRoute == Routes.BACKUP_RESTORE,
            onClick = { onNavigate(Routes.BACKUP_RESTORE) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text("Log Out") },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
