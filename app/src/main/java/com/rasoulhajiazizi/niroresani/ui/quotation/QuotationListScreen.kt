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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationListScreen(
    onBack: () -> Unit,
    onQuotationClick: (Long) -> Unit,
    viewModel: QuotationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var quotationToDelete by remember { mutableStateOf<QuotationEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پیش‌فاکتورها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("جستجو بر اساس شماره یا نام مشتری") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (uiState.quotations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("هنوز پیش‌فاکتوری ثبت نشده است")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.quotations, key = { it.id }) { quotation ->
                        QuotationRow(
                            quotation = quotation,
                            onClick = { onQuotationClick(quotation.id) },
                            onDeleteClick = { quotationToDelete = quotation }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    quotationToDelete?.let { quotation ->
        AlertDialog(
            onDismissRequest = { quotationToDelete = null },
            title = { Text("حذف پیش‌فاکتور") },
            text = { Text("آیا از حذف پیش‌فاکتور شماره ${PersianNumberFormatter.toPersianDigits(quotation.number)} اطمینان دارید؟") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteQuotation(quotation)
                    quotationToDelete = null
                }) { Text("حذف") }
            },
            dismissButton = {
                TextButton(onClick = { quotationToDelete = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun QuotationRow(quotation: QuotationEntity, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    val customerName = remember(quotation.customerSnapshotJson) {
        try {
            val json = JSONObject(quotation.customerSnapshotJson)
            "${json.optString("firstName")} ${json.optString("lastName")}"
        } catch (e: Exception) {
            ""
        }
    }

    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("شماره ${PersianNumberFormatter.toPersianDigits(quotation.number)}", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(quotation.issueDateShamsi, style = MaterialTheme.typography.bodySmall)
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف پیش‌فاکتور")
                    }
                }
            }
            Text(customerName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                PersianNumberFormatter.formatRial(quotation.totalAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
