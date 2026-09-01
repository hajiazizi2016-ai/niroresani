package com.rasoulhajiazizi.niroresani.ui.backup

import android.content.Context
import android.net.Uri
import com.rasoulhajiazizi.niroresani.core.database.AppDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * مدیریت پشتیبان‌گیری و بازیابی کامل اطلاعات برنامه (فاز ۸ - بخش ۷۹، ۱۳۳ سند).
 *
 * روش کار: دیتابیس Room در حالت WAL کار می‌کند، یعنی همیشه سه فایل دارد
 * (اصلی db + wal + shm). هر سه در یک فایل ZIP بسته‌بندی می‌شوند تا هیچ
 * تراکنش نیمه‌کاره‌ای در بازیابی از دست نرود.
 *
 * نکته امنیتی مهم: بعد از بازیابی، برنامه باید کامل ری‌استارت شود چون
 * Room نمی‌تواند به‌صورت امن فایل دیتابیس زیرینش را در حین اجرا عوض کند.
 */
object BackupManager {

    private const val DB_ENTRY = "database.db"
    private const val WAL_ENTRY = "database.db-wal"
    private const val SHM_ENTRY = "database.db-shm"

    fun suggestedFileName(): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
        return "Backup_NiroResani_$timestamp.zip"
    }

    /** ایجاد فایل پشتیبان و بازگرداندن مسیر آن (برای اشتراک‌گذاری) */
    fun createBackup(context: Context): File {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        val walFile = File(dbFile.path + "-wal")
        val shmFile = File(dbFile.path + "-shm")

        val backupDir = File(context.filesDir, "backup").apply { mkdirs() }
        val outFile = File(backupDir, suggestedFileName())

        ZipOutputStream(FileOutputStream(outFile)).use { zip ->
            addFileToZip(zip, dbFile, DB_ENTRY)
            if (walFile.exists()) addFileToZip(zip, walFile, WAL_ENTRY)
            if (shmFile.exists()) addFileToZip(zip, shmFile, SHM_ENTRY)
        }
        return outFile
    }

    /**
     * بازیابی از فایل پشتیبان انتخاب‌شده توسط کاربر.
     * فایل‌های فعلی دیتابیس جایگزین می‌شوند. فراخوانی‌کننده مسئول است که
     * بلافاصله بعد از این تابع، فرآیند برنامه را ری‌استارت کند.
     */
    fun restoreBackup(context: Context, sourceUri: Uri) {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        val walFile = File(dbFile.path + "-wal")
        val shmFile = File(dbFile.path + "-shm")

        // پاک‌سازی فایل‌های WAL/SHM قدیمی قبل از بازیابی، تا با نسخه جدید تداخل نکنند
        walFile.delete()
        shmFile.delete()

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            ZipInputStream(input).use { zip ->
                var entry: ZipEntry? = zip.nextEntry
                while (entry != null) {
                    val targetFile = when (entry.name) {
                        DB_ENTRY -> dbFile
                        WAL_ENTRY -> walFile
                        SHM_ENTRY -> shmFile
                        else -> null
                    }
                    if (targetFile != null) {
                        targetFile.parentFile?.mkdirs()
                        FileOutputStream(targetFile).use { output -> zip.copyTo(output) }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } ?: throw IllegalStateException("فایل انتخاب‌شده قابل خواندن نیست")
    }

    private fun addFileToZip(zip: ZipOutputStream, file: File, entryName: String) {
        zip.putNextEntry(ZipEntry(entryName))
        FileInputStream(file).use { input -> input.copyTo(zip) }
        zip.closeEntry()
    }
}
