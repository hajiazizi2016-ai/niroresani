package com.rasoulhajiazizi.niroresani.ui.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onBack: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("امنیت") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("رمز ورود به برنامه", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = uiState.isPasswordEnabled,
                    onCheckedChange = { enabled ->
                        if (!enabled) viewModel.disablePassword()
                        // فعال کردن مستقیم از طریق سوییچ انجام نمی‌شود؛ کاربر باید فرم پایین را پر و ذخیره کند
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.hasExistingPassword) {
                OutlinedTextField(
                    value = uiState.currentPasswordInput,
                    onValueChange = viewModel::onCurrentPasswordChange,
                    label = { Text("رمز فعلی") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("برای تغییر رمز، رمز جدید را وارد کنید:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text("برای فعال کردن رمز ورود، یک رمز تعیین کنید:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = uiState.newPasswordInput,
                onValueChange = viewModel::onNewPasswordChange,
                label = { Text("رمز جدید") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.confirmPasswordInput,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("تکرار رمز جدید") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { viewModel.setOrChangePassword() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (uiState.hasExistingPassword) "ذخیره رمز جدید" else "فعال‌سازی رمز ورود")
            }

            if (uiState.hasExistingPassword) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = { viewModel.disablePassword() }, modifier = Modifier.fillMaxWidth()) {
                    Text("غیرفعال کردن رمز ورود")
                }
            }
        }
    }
}
