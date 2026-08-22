package com.rasoulhajiazizi.niroresani.ui.catalog

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity

enum class CatalogMode { BROWSE, SELECT_FOR_QUOTATION }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    mode: CatalogMode = CatalogMode.BROWSE,
    onBack: () -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    onReviewQuotationClick: () -> Unit = {},
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val draftItemCount by viewModel.draftItemCount.collectAsState()
    var itemToEditPrice by remember { mutableStateOf<CatalogItemEntity?>(null) }
    var itemToAddQuantity by remember { mutableStateOf<CatalogItemEntity?>(null) }

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
        },
        bottomBar = {
            if (mode == CatalogMode.SELECT_FOR_QUOTATION && draftItemCount > 0) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$draftItemCount قلم انتخاب شده")
                        Button(onClick = onReviewQuotationClick) { Text("مشاهده و ادامه پیش‌فاکتور") }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("جستجوی تجهیزات") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
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
                            CategoryRow(category = category, onClick = { onCategoryClick(category.id, category.title) })
                        }
                    }
                    if (uiState.items.isNotEmpty()) {
                        items(uiState.items, key = { "item_${it.id}" }) { item ->
                            CatalogItemRow(
                                item = item,
                                unitTitle = uiState.unitTitleById[item.unitId] ?: "",
                                mode = mode,
                                onEditPriceClick = { itemToEditPrice = item },
                                onSelectClick = { itemToAddQuantity = item }
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
            onConfirm = { newPrice -> viewModel.updatePrice(item, newPrice) { itemToEditPrice = null } }
        )
    }

    itemToAddQuantity?.let { item ->
        QuantityEntryDialog(
            item = item,
            unitTitle = uiState.unitTitleById[item.unitId] ?: "",
            onDismiss = { itemToAddQuantity = null },
            onConfirm = { quantity ->
                viewModel.addItemToQuotation(item, quantity)
                itemToAddQuantity = null
            }
        )
    }
}

@Composable
private fun CategoryRow(category: CategoryEntity, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
    mode: CatalogMode,
    onEditPriceClick: () -> Unit,
    onSelectClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (mode == CatalogMode.SELECT_FOR_QUOTATION) onSelectClick else ({})
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val priceText = if (item.currentPrice <= 0L) "قیمت ثبت نشده است"
                else "${PersianNumberFormatter.formatRial(item.currentPrice)} / $unitTitle"
                Text(
                    priceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.currentPrice <= 0L) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (mode == CatalogMode.BROWSE) {
                IconButton(onClick = onEditPriceClick) {
                    Icon(Icons.Default.Edit, contentDescription = "ویرایش قیمت")
                }
            } else {
                IconButton(onClick = onSelectClick) {
                    Icon(Icons.Default.AddCircle, contentDescription = "افزودن به پیش‌فاکتور")
                }
            }
        }
    }
}

@Composable
private fun PriceEditDialog(item: CatalogItemEntity, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var priceInput by remember { mutableStateOf(if (item.currentPrice > 0) item.currentPrice.toString() else "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ویرایش قیمت: ${item.title}") },
        text = {
            OutlinedTextField(
                value = priceInput,
                onValueChange = { priceInput = it; error = null },
                label = { Text("قیمت جدید (ریال)") },
                isError = error != null,
                supportingText = { error?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val validation = InputValidators.validatePrice(priceInput)
                if (validation is InputValidators.ValidationResult.Invalid) error = validation.message
                else onConfirm(priceInput)
            }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun QuantityEntryDialog(
    item: CatalogItemEntity,
    unitTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var quantityInput by remember { mutableStateOf("1") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.title) },
        text = {
            Column {
                Text(
                    "قیمت واحد: ${PersianNumberFormatter.formatRial(item.currentPrice)} / $unitTitle",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it; error = null },
                    label = { Text("تعداد ($unitTitle)") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val validation = InputValidators.validateQuantity(quantityInput)
                if (validation is InputValidators.ValidationResult.Invalid) {
                    error = validation.message
                } else {
                    val normalized = PersianNumberFormatter.toLatinDigits(quantityInput).trim().toDouble()
                    onConfirm(normalized)
                }
            }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
