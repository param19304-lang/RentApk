package com.example.rentmanagement.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rentmanagement.ui.components.ConfirmDialog
import com.example.rentmanagement.ui.components.DangerButton
import com.example.rentmanagement.ui.components.PrimaryButton
import com.example.rentmanagement.ui.components.SectionHeader
import com.example.rentmanagement.ui.theme.Radius
import com.example.rentmanagement.ui.theme.Spacing
import com.example.rentmanagement.utils.BackupManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onBack: () -> Unit,
    viewModel: BackupRestoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val status by viewModel.status.collectAsState()
    val restored by viewModel.restoredSuccessfully.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { viewModel.exportBackup(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) pendingRestoreUri = uri }

    LaunchedEffect(restored) {
        if (restored) {
            delay(1200)
            BackupManager.restartApp(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(Spacing.lg)) {
            SectionHeader("Backup")
            Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Spacing.lg)) {
                    Text(
                        "Save a copy of all your properties, tenants, leases, rent, payments, and expenses to a file you choose.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(Spacing.md))
                    PrimaryButton(
                        text = "Export Backup",
                        onClick = { exportLauncher.launch("rent-manager-backup-${System.currentTimeMillis()}.db") },
                        enabled = !isBusy
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xxl))
            SectionHeader("Restore")
            Card(shape = RoundedCornerShape(Radius.card), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Spacing.lg)) {
                    Text(
                        "Restoring replaces all current data with the contents of the backup file. This cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(Spacing.md))
                    DangerButton(
                        text = "Import Backup",
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        enabled = !isBusy
                    )
                }
            }

            if (status != null) {
                Spacer(Modifier.height(Spacing.lg))
                Text(status!!, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (pendingRestoreUri != null) {
        ConfirmDialog(
            title = "Restore backup?",
            message = "This will overwrite all current data with the selected backup file and restart the app. This cannot be undone.",
            confirmLabel = "Restore",
            onConfirm = {
                viewModel.importBackup(pendingRestoreUri!!)
                pendingRestoreUri = null
            },
            onDismiss = { pendingRestoreUri = null }
        )
    }
}
