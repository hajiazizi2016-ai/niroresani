package com.rasoulhajiazizi.niroresani.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * یک آیتم در منوی اصلی. اگر submenu خالی باشد، با لمس مستقیماً اقدام انجام می‌شود؛
 * در غیر این صورت زیرمنوی کشویی باز می‌شود - بخش ۴ درخواست اصلاحات.
 */
data class HomeMenuItem(
    val key: String,
    val title: String,
    val icon: ImageVector,
    val submenu: List<HomeSubmenuItem> = emptyList()
)

data class HomeSubmenuItem(val key: String, val title: String)

private val mainMenuItems = listOf(
    HomeMenuItem(
        key = "customers",
        title = "مشتریان",
        icon = Icons.Default.People,
        submenu = listOf(HomeSubmenuItem("customers_list", "مشاهده لیست مشتریان"))
    ),
    HomeMenuItem(
        key = "quotation_new",
        title = "پیش‌فاکتور جدید",
        icon = Icons.Default.Add
    ),
    HomeMenuItem(
        key = "invoices",
        title = "فاکتورها",
        icon = Icons.Default.Receipt,
        submenu = listOf(HomeSubmenuItem("invoices_list", "مشاهده لیست فاکتورها"))
    ),
    HomeMenuItem(
        key = "reports",
        title = "گزارشات",
        icon = Icons.Default.Assessment,
        submenu = listOf(HomeSubmenuItem("reports_soon", "به‌زودی"))
    ),
    HomeMenuItem(
        key = "warehouse",
        title = "انبار",
        icon = Icons.Default.Inventory2,
        submenu = listOf(HomeSubmenuItem("warehouse_soon", "به‌زودی"))
    )
)

@Composable
fun HomeScreen(
    onMenuItemClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onContactDeveloperClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedKey by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            HomeTopBar(
                timeText = uiState.currentTimeText,
                dateText = uiState.currentDateShamsi,
                onSettingsClick = onSettingsClick,
                onContactDeveloperClick = onContactDeveloperClick
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(mainMenuItems) { item ->
                    MainMenuButton(
                        item = item,
                        isExpanded = expandedKey == item.key,
                        onClick = {
                            if (item.submenu.isEmpty()) {
                                onMenuItemClick(item.key)
                            } else {
                                expandedKey = if (expandedKey == item.key) null else item.key
                            }
                        }
                    )
                }
            }

            mainMenuItems.firstOrNull { it.key == expandedKey }?.let { expandedItem ->
                AnimatedVisibility(visible = true, enter = expandVertically(), exit = shrinkVertically()) {
                    SubmenuPanel(
                        items = expandedItem.submenu,
                        onItemClick = { subKey ->
                            expandedKey = null
                            onMenuItemClick(subKey)
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun HomeTopBar(
    timeText: String,
    dateText: String,
    onSettingsClick: () -> Unit,
    onContactDeveloperClick: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "تنظیمات", tint = MaterialTheme.colorScheme.onPrimary)
                }
                IconButton(onClick = onContactDeveloperClick) {
                    Icon(Icons.Default.ContactMail, contentDescription = "ارتباط با سازنده اپلیکیشن", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MainMenuButton(item: HomeMenuItem, isExpanded: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                item.icon,
                contentDescription = item.title,
                tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            item.title,
            style = MaterialTheme.typography.labelLarge,
            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SubmenuPanel(items: List<HomeSubmenuItem>, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        items.forEach { sub ->
            Card(
                onClick = { onItemClick(sub.key) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(sub.title, style = MaterialTheme.typography.bodyLarge)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                }
            }
        }
    }
}
