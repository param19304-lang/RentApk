package com.example.rentmanagement.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.rentmanagement.data.database.AppDatabase
import java.io.File

/**
 * Local backup/restore by copying the Room SQLite file directly (spec section 16).
 * Kept as raw file I/O rather than a repository since it operates below the
 * repository layer (on the database file itself), not on domain data.
 */
object BackupManager {

    fun databaseFile(context: Context): File = context.getDatabasePath(AppDatabase.DATABASE_NAME)

    fun exportTo(context: Context, database: AppDatabase, destination: Uri): Boolean = try {
        database.query("PRAGMA wal_checkpoint(FULL)", null).close()
        context.contentResolver.openOutputStream(destination)?.use { out ->
            databaseFile(context).inputStream().use { input -> input.copyTo(out) }
        }
        true
    } catch (e: Exception) {
        false
    }

    fun importFrom(context: Context, database: AppDatabase, source: Uri): Boolean = try {
        database.close()
        context.contentResolver.openInputStream(source)?.use { input ->
            databaseFile(context).outputStream().use { output -> input.copyTo(output) }
        }
        File(databaseFile(context).path + "-wal").delete()
        File(databaseFile(context).path + "-shm").delete()
        true
    } catch (e: Exception) {
        false
    }

    fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}
