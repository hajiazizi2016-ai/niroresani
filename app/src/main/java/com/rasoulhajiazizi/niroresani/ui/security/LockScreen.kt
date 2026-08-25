package com.rasoulhajiazizi.niroresani.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordInput by remember { mutableStateOf("") }

    if (uiState.isUnlocked) {
        LaunchedEffect(Unit) { onUnlocked() }
        return
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("برنامه قفل است", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; viewModel.clearError() },
                label = { Text("رمز عبور") },
                singleLine = true,
                isError = uiState.errorMessage != null,
                supportingText = { uiState.errorMessage?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.tryUnlock(passwordInput) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ورود")
            }
        }
    }
}
