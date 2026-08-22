package com.rasoulhajiazizi.niroresani.ui.customer

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    viewModel: CustomerListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var customerToDelete by remember { mutableStateOf<CustomerEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مشتریان") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "افزودن مشتری")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("جستجوی مشتری") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (uiState.customers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (uiState.query.isBlank()) "هنوز مشتری‌ای ثبت نشده است" else "موردی یافت نشد",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.customers, key = { it.id }) { customer ->
                        CustomerRow(
                            customer = customer,
                            onClick = { onCustomerClick(customer.id) },
                            onDeleteClick = { customerToDelete = customer }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    customerToDelete?.let { customer ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("حذف مشتری") },
            text = { Text("آیا از حذف «${customer.firstName} ${customer.lastName}» اطمینان دارید؟") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCustomer(customer)
                    customerToDelete = null
                }) { Text("حذف") }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun CustomerRow(customer: CustomerEntity, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${customer.firstName} ${customer.lastName}", style = MaterialTheme.typography.titleMedium)
                if (customer.address.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        customer.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "حذف مشتری")
            }
        }
    }
}
