package com.rasoulhajiazizi.niroresani.ui.quotation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity
import com.rasoulhajiazizi.niroresani.ui.pdf.QuotationPdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationDetailScreen(
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: QuotationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val quotation = uiState.quotation
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGeneratingPdf by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        quotation?.let { "پیش‌فاکتور شماره ${PersianNumberFormatter.toPersianDigits(it.number)}" }
                            ?: "پیش‌فاکتور"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    if (quotation != null) {
                        IconButton(
                            enabled = !isGeneratingPdf,
                            onClick = {
                                isGeneratingPdf = true
                                coroutineScope.launch {
                                    try {
                                        val result = withContext(Dispatchers.IO) {
                                            QuotationPdfGenerator.generate(
                                                context = context,
                                                quotation = quotation,
                                                items = uiState.items,
                                                companyName = uiState.companyName,
                                                registrationNumber = uiState.companyRegistrationNumber,
                                                logoPath = uiState.companyLogoPath,
                                                customerName = uiState.customerName,
                                                customerAddress = uiState.customerAddress
                                            )
                                        }
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            result.file
                                        )
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری پیش‌فاکتور"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "خطا در تولید PDF: ${e.message}", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isGeneratingPdf = false
                                    }
                                }
                            }
                        ) {
                            if (isGeneratingPdf) {
                                CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                            } else {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "خروجی PDF")
                            }
                        }
                        IconButton(onClick = {
                            viewModel.startEdit()
                            onEditClick()
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "ویرایش پیش‌فاکتور")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (quotation == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
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
