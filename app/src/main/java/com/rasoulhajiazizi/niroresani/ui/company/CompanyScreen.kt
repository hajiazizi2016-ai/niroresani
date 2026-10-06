package com.rasoulhajiazizi.niroresani.ui.company

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rasoulhajiazizi.niroresani.ui.common.UnsavedChangesGuard
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen(
    onBack: () -> Unit,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingExitAfterSave by remember { mutableStateOf(false) }

    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = copyImageToInternalStorage(context, it, "logo", "company_logo.jpg")
            if (savedPath != null) viewModel.onLogoPicked(savedPath)
        }
    }

    val signaturePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = copyImageToInternalStorage(context, it, "signature", "company_signature.jpg")
            if (savedPath != null) viewModel.onSignaturePicked(savedPath)
        }
    }

    LaunchedEffect(uiState.savedMessage) {
        uiState.savedMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // بعد از پایان موفق ذخیره (که از مسیر «ذخیره و خروج» شروع شده)، از صفحه خارج شو
    LaunchedEffect(uiState.isSaving, pendingExitAfterSave) {
        if (pendingExitAfterSave && !uiState.isSaving && uiState.nameError == null && uiState.savedMessage != null) {
            onBack()
        }
    }

    // هشدار خروج با اطلاعات ذخیره‌نشده - الزام صریح بخش ۱۷ سند
    UnsavedChangesGuard(
        hasUnsavedChanges = uiState.hasUnsavedChanges,
        onSaveAndExit = {
            if (uiState.name.isNotBlank()) {
                pendingExitAfterSave = true
            }
            viewModel.save()
        },
        onExitWithoutSaving = onBack
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("اطلاعات شرکت") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ImagePicker(
                    modifier = Modifier.weight(1f),
                    label = "لوگوی شرکت",
                    imagePath = uiState.logoPath,
                    onPickClick = { logoPicker.launch("image/*") },
                    onRemoveClick = { viewModel.onLogoRemoved() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                ImagePicker(
                    modifier = Modifier.weight(1f),
                    label = "امضای زیر سند",
                    imagePath = uiState.signaturePath,
                    onPickClick = { signaturePicker.launch("image/*") },
                    onRemoveClick = { viewModel.onSignatureRemoved() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("نام شرکت") },
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.registrationNumber,
                onValueChange = viewModel::onRegistrationNumberChange,
                label = { Text("شماره ثبت") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.nationalId,
                onValueChange = viewModel::onNationalIdChange,
                label = { Text("شناسه ملی") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.economicCode,
                onValueChange = viewModel::onEconomicCodeChange,
                label = { Text("شماره اقتصادی") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.postalCode,
                onValueChange = viewModel::onPostalCodeChange,
                label = { Text("کدپستی") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.mobile,
                    onValueChange = viewModel::onMobileChange,
                    label = { Text("تلفن همراه") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = uiState.fax,
                    onValueChange = viewModel::onFaxChange,
                    label = { Text("نمابر") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("آدرس") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.save() },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isSaving) "در حال ذخیره..." else "ذخیره اطلاعات")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ImagePicker(
    modifier: Modifier = Modifier,
    label: String,
    imagePath: String?,
    onPickClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .clickable(onClick = onPickClick),
            contentAlignment = Alignment.Center
        ) {
            if (imagePath != null) {
                AsyncImage(
                    model = File(imagePath),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Upload, contentDescription = null)
            }
        }
        if (imagePath != null) {
            TextButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("حذف", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun copyImageToInternalStorage(
    context: Context,
    uri: Uri,
    folderName: String,
    fileName: String
): String? {
    return try {
        val dir = File(context.filesDir, folderName).apply { mkdirs() }
        val destFile = File(dir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output -> input.copyTo(output) }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
