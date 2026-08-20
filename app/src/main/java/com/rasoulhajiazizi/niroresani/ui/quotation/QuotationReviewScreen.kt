package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationReviewScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: QuotationReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("بازبینی پیش‌فاکتور") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جمع کل:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            PersianNumberFormatter.formatRial(uiState.totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.save() },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isSaving) "در حال ذخیره..." else "ذخیره نهایی پیش‌فاکتور")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "مشتری: ${uiState.customerLabel}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("هنوز آیتمی انتخاب نشده است")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.items) { index, item ->
                        DraftItemRow(
                            item = item,
                            onQuantityChange = { newValue -> viewModel.onQuantityChange(index, newValue) },
                            onRemoveClick = { viewModel.onRemoveItem(index) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("توضیحات و شرایط پروژه") },
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun DraftItemRow(
    item: DraftItem,
    onQuantityChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    var quantityText by remember(item.catalogItemId) { mutableStateOf(item.quantity.toString()) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it; onQuantityChange(it) },
                    label = { Text("تعداد (${item.unit})") },
                    singleLine = true,
                    modifier = Modifier.width(140.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "قیمت واحد: ${PersianNumberFormatter.formatRial(item.unitPrice)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "مبلغ: ${PersianNumberFormatter.formatRial(item.lineTotal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
