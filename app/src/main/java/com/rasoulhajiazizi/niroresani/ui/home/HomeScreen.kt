package com.rasoulhajiazizi.niroresani.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.ui.theme.DateDisplayStyle

data class HomeMenuItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val mainMenuItems = listOf(
    HomeMenuItem("شرکت", Icons.Default.Apartment),
    HomeMenuItem("مشتری", Icons.Default.Person),
    HomeMenuItem("تجهیزات", Icons.Default.Inventory),
    HomeMenuItem("پیش‌فاکتور", Icons.Default.Description)
)

@Composable
fun HomeScreen(
    onMenuItemClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onContactDeveloperClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNewQuotationClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                dateText = uiState.currentDateShamsi,
                onSettingsClick = onSettingsClick,
                onContactDeveloperClick = onContactDeveloperClick
            )
        },
        bottomBar = {
            HomeBottomBar(onSearchClick = onSearchClick, onNewQuotationClick = onNewQuotationClick)
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            items(mainMenuItems) { item ->
                MainMenuCard(item = item, onClick = { onMenuItemClick(item.title) })
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    dateText: String,
    onSettingsClick: () -> Unit,
    onContactDeveloperClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "تنظیمات")
            }
            IconButton(onClick = onContactDeveloperClick) {
                Icon(Icons.Default.ContactMail, contentDescription = "ارتباط با سازنده اپلیکیشن")
            }
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = dateText, style = DateDisplayStyle, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
private fun HomeBottomBar(onSearchClick: () -> Unit, onNewQuotationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, contentDescription = "جستجوی پیش‌فاکتورها")
        }
        ExtendedFloatingActionButton(
            onClick = onNewQuotationClick,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("پیش‌فاکتور جدید") }
        )
    }
}

@Composable
private fun MainMenuCard(item: HomeMenuItem, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth().height(96.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
