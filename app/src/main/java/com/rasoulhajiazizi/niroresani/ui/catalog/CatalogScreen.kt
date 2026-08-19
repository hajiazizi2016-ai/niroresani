package com.rasoulhajiazizi.niroresani.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onBack: () -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var itemToEditPrice by remember { mutableStateOf<CatalogItemEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("جستجوی تجهیزات") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            val nothingToShow = uiState.childCategories.isEmpty() && uiState.items.isEmpty()

            if (nothingToShow) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (uiState.isSearching) "موردی یافت نشد" else "این دسته خالی است",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!uiState.isSearching && uiState.childCategories.isNotEmpty()) {
                        items(uiState.childCategories, key = { "cat_${it.id}" }) { category ->
                            CategoryRow(
                                category = category,
                                onClick = { onCategoryClick(category.id, category.title) }
                            )
                        }
                    }
                    if (uiState.items.isNotEmpty()) {
                        items(uiState.items, key = { "item_${it.id}" }) { item ->
                            CatalogItemRow(
                                item = item,
                                unitTitle = uiState.unitTitleById[item.unitId] ?: "",
                                onEditPriceClick = { itemToEditPrice = item }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    itemToEditPrice?.let { item ->
        PriceEditDialog(
            item = item,
            onDismiss = { itemToEditPrice = null },
            onConfirm = { newPrice ->
                viewModel.updatePrice(item, newPrice) { itemToEditPrice = null }
            }
        )
    }
}

@Composable
private fun CategoryRow(category: CategoryEntity, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Folder, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(category.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronLeft, contentDescription = null)
        }
    }
}

@Composable
private fun CatalogItemRow(
    item: CatalogItemEntity,
    unitTitle: String,
    onEditPriceClick: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val priceText = if (item.currentPrice <= 0L) {
                    "قیمت ثبت نشده است"
                } else {
                    "${PersianNumberFormatter.formatRial(item.currentPrice)} / $unitTitle"
                }
                Text(
                    priceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.currentPrice <= 0L) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditPriceClick) {
                Icon(Icons.Default.Edit, contentDescription = "ویرایش قیمت")
            }
        }
    }
}

@Composable
private fun PriceEditDialog(
    item: CatalogItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var priceInput by remember {
        mutableStateOf(if (item.currentPrice > 0) item.currentPrice.toString() else "")
    }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ویرایش قیمت: ${item.title}") },
        text = {
            Column {
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it; error = null },
                    label = { Text("قیمت جدید (ریال)") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val validation = InputValidators.validatePrice(priceInput)
                if (validation is InputValidators.ValidationResult.Invalid) {
                    error = validation.message
                } else {
                    onConfirm(priceInput)
                }
            }) { Text("ذخیره") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
