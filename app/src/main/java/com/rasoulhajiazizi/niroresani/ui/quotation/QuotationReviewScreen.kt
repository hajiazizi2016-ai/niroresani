package com.rasoulhajiazizi.niroresani.ui.quotation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.ui.common.UnsavedChangesGuard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationReviewScreen(
    onBack: () -> Unit,
    onSaved: (quotationId: Long, wasEditing: Boolean) -> Unit,
    onDiscardDraft: () -> Unit,
    viewModel: QuotationReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved && uiState.savedQuotationId != null) {
            onSaved(uiState.savedQuotationId!!, uiState.isEditing)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // هشدار خروج با اطلاعات ذخیره‌نشده - الزام صریح بخش ۱۷ سند.
    // «خروج بدون ذخیره» پیش‌نویس را کامل پاک کرده و به صفحه اصلی برمی‌گردد.
    UnsavedChangesGuard(
        hasUnsavedChanges = uiState.items.isNotEmpty() && !uiState.isSaved,
        onSaveAndExit = { viewModel.save() },
        onExitWithoutSaving = {
            viewModel.discardDraft()
            onDiscardDraft()
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "ویرایش پیش‌فاکتور" else "بازبینی پیش‌فاکتور") },
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Text(
                            if (uiState.isSaving) "در حال ذخیره..."
                            else if (uiState.isEditing) "ذخیره تغییرات"
                            else "ذخیره نهایی پیش‌فاکتور"
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
private fun DraftItemRow(item: DraftItem, onQuantityChange: (String) -> Unit, onRemoveClick: () -> Unit) {
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
