package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationCustomerPickerScreen(
    onBack: () -> Unit,
    onAddCustomerClick: () -> Unit,
    onCustomerSelected: (Long, String) -> Unit,
    viewModel: QuotationCustomerPickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("انتخاب مشتری برای پیش‌فاکتور") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCustomerClick) {
                Icon(Icons.Default.Add, contentDescription = "مشتری جدید")
            }
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
                label = { Text("جستجوی مشتری") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (uiState.customers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("مشتری‌ای یافت نشد. ابتدا با دکمه + یک مشتری ثبت کنید.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.customers, key = { it.id }) { customer ->
                        ElevatedCard(
                            onClick = {
                                viewModel.selectCustomer(customer.id, "${customer.firstName} ${customer.lastName}")
                                onCustomerSelected(customer.id, "${customer.firstName} ${customer.lastName}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${customer.firstName} ${customer.lastName}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }
}
