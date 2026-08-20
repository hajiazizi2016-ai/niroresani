package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationDetailScreen(
    onBack: () -> Unit,
    viewModel: QuotationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val quotation = uiState.quotation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quotation?.let { "پیش‌فاکتور شماره ${PersianNumberFormatter.toPersianDigits(it.number)}" } ?: "پیش‌فاکتور") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        if (quotation == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(uiState.companyName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(quotation.issueDateShamsi, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("مشتری: ${uiState.customerName}", style = MaterialTheme.typography.titleMedium)
            if (uiState.customerAddress.isNotBlank()) {
                Text(uiState.customerAddress, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.items) { item -> QuotationItemDetailRow(item) }
            }

            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("جمع کل نهایی:", style = MaterialTheme.typography.titleMedium)
                Text(
                    PersianNumberFormatter.formatRial(quotation.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            quotation.description?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("توضیحات:", fontWeight = FontWeight.Bold)
                    Text(it)
                }
            }
        }
    }
}

@Composable
private fun QuotationItemDetailRow(item: QuotationItemEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(item.titleSnapshot, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${PersianNumberFormatter.toPersianDigits(item.quantity.toString())} ${item.unitSnapshot} × ${PersianNumberFormatter.formatRial(item.unitPriceSnapshot)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(PersianNumberFormatter.formatRial(item.lineTotal), fontWeight = FontWeight.Bold)
        }
    }
}
