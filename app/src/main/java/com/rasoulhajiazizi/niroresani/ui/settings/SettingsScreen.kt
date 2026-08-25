package com.rasoulhajiazizi.niroresani.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class SettingsItem(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onCompanyClick: () -> Unit,
    onSecurityClick: () -> Unit
) {
    val items = listOf(
        SettingsItem("اطلاعات شرکت", Icons.Default.Apartment, "company"),
        SettingsItem("امنیت", Icons.Default.Security, "security")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items) { item ->
                ElevatedCard(
                    onClick = { if (item.route == "company") onCompanyClick() else onSecurityClick() },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(item.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            }
        }
    }
}
