package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("جستجو بر اساس شماره یا نام مشتری") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                        QuotationRow(quotation = quotation, onClick = { onQuotationClick(quotation.id) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun QuotationRow(quotation: QuotationEntity, onClick: () -> Unit) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("شماره ${PersianNumberFormatter.toPersianDigits(quotation.number)}", fontWeight = FontWeight.Bold)
                Text(quotation.issueDateShamsi, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
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
