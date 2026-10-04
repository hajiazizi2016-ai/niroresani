package com.rasoulhajiazizi.niroresani.ui.contact

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private const val DEVELOPER_NAME = "رسول حاجی عزیزی"
private const val WHATSAPP_NUMBER = "09143467035"
private const val TELEGRAM_USERNAME = "amir_hajiazizi"

private fun openLink(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "برنامه‌ای برای باز کردن این لینک پیدا نشد", Toast.LENGTH_SHORT).show()
    }
}

private data class ContactItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDeveloperScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val whatsappDigits = WHATSAPP_NUMBER
        .replace(" ", "")
        .let { if (it.startsWith("0")) "98" + it.substring(1) else it }

    val items = listOf(
        ContactItem(
            title = "واتس‌اپ",
            subtitle = WHATSAPP_NUMBER,
            icon = Icons.Default.Call,
            onClick = { openLink(context, "https://wa.me/$whatsappDigits") }
        ),
        ContactItem(
            title = "تلگرام",
            subtitle = "@$TELEGRAM_USERNAME",
            icon = Icons.Default.Send,
            onClick = { openLink(context, "https://t.me/$TELEGRAM_USERNAME") }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ارتباط با سازنده") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(32.dp))
Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("طراحی و توسعه", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(DEVELOPER_NAME, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            items.forEach { item ->
                ElevatedCard(
                    onClick = item.onClick,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.subtitle, style = MaterialTheme.typography.bodyMedium)
                        }
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            }
        }
    }
}