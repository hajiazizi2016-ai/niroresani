package com.rasoulhajiazizi.niroresani.ui.backup

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }

    val restoreFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingRestoreUri = uri
            viewModel.onRestoreFileSelected()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("پشتیبان‌گیری و بازیابی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {

            Text("ایجاد نسخه پشتیبان", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "یک فایل کامل از تمام اطلاعات برنامه (شرکت، مشتریان، تجهیزات، قیمت‌ها و پیش‌فاکتورها) ساخته می‌شود تا در جایی امن (تلگرام، ایمیل، حافظه ابری) ذخیره کنید.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                enabled = !uiState.isWorking,
                onClick = {
                    viewModel.setWorking(true)
                    coroutineScope.launch {
                        try {
                            val file = withContext(Dispatchers.IO) { BackupManager.createBackup(context) }
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/zip"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "ذخیره نسخه پشتیبان"))
                            viewModel.setWorking(false)
                        } catch (e: Exception) {
                            viewModel.setError("خطا در ایجاد نسخه پشتیبان: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ایجاد و اشتراک‌گذاری نسخه پشتیبان")
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text("بازیابی از نسخه پشتیبان", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "با انتخاب یک فایل پشتیبان قبلی، تمام اطلاعات فعلی برنامه با اطلاعات آن فایل جایگزین می‌شود.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                enabled = !uiState.isWorking,
                onClick = { restoreFilePicker.launch(arrayOf("application/zip", "*/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("انتخاب فایل پشتیبان برای بازیابی")
            }

            if (uiState.isWorking) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }

    if (uiState.showRestoreConfirm && pendingRestoreUri != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRestoreConfirm(); pendingRestoreUri = null },
            title = { Text("بازیابی اطلاعات") },
            text = { Text("با بازیابی این فایل، تمام اطلاعات فعلی برنامه (شرکت، مشتریان، تجهیزات، قیمت‌ها، پیش‌فاکتورها) حذف و با اطلاعات فایل انتخاب‌شده جایگزین می‌شود. این عملیات قابل بازگشت نیست. آیا ادامه می‌دهید؟") },
            confirmButton = {
                TextButton(onClick = {
                    val uri = pendingRestoreUri ?: return@TextButton
                    viewModel.dismissRestoreConfirm()
                    viewModel.setWorking(true)
                    coroutineScope.launch {
                        try {
                            withContext(Dispatchers.IO) { BackupManager.restoreBackup(context, uri) }
                            Toast.makeText(context, "بازیابی انجام شد. برنامه ری‌استارت می‌شود...", Toast.LENGTH_SHORT).show()
                            restartApp(context)
                        } catch (e: Exception) {
                            viewModel.setError("خطا در بازیابی: ${e.message}")
                        }
                    }
                }) { Text("بله، بازیابی کن") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRestoreConfirm(); pendingRestoreUri = null }) { Text("انصراف") }
            }
        )
    }
}

/** ری‌استارت کامل فرآیند برنامه تا Room دیتابیس بازیابی‌شده را از صفر بخواند */
private fun restartApp(context: android.content.Context) {
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    android.os.Process.killProcess(android.os.Process.myPid())
}
