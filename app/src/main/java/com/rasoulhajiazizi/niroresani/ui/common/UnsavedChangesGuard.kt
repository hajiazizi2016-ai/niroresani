package com.rasoulhajiazizi.niroresani.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * دیالوگ استاندارد هشدار خروج با اطلاعات ذخیره‌نشده - عیناً طبق بخش ۱۷ سند:
 * «آیا اطلاعات ذخیره شود؟» با سه گزینه: ذخیره و خروج / خروج بدون ذخیره / انصراف.
 */
@Composable
fun UnsavedChangesGuard(
    hasUnsavedChanges: Boolean,
    onSaveAndExit: () -> Unit,
    onExitWithoutSaving: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = hasUnsavedChanges) {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("اطلاعات ذخیره نشده") },
            text = { Text("تغییراتی ثبت نشده وجود دارد. آیا اطلاعات ذخیره شود؟") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onSaveAndExit()
                }) { Text("ذخیره و خروج") }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = {
                        showDialog = false
                        onExitWithoutSaving()
                    }) { Text("خروج بدون ذخیره") }
                    TextButton(onClick = { showDialog = false }) { Text("انصراف") }
                }
            }
        )
    }
}
