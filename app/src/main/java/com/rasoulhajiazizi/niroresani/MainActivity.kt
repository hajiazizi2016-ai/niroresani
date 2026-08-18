package com.rasoulhajiazizi.niroresani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rasoulhajiazizi.niroresani.ui.home.HomeScreen
import com.rasoulhajiazizi.niroresani.ui.splash.SplashScreen
import com.rasoulhajiazizi.niroresani.ui.theme.NiroResaniTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NiroResaniTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

/**
 * ریشه ناوبری برنامه.
 * فاز فعلی (۰ و ۱): فقط Splash → صفحه اصلی.
 * از فاز ۲ به بعد، NavHost کامل (Navigation Compose) جایگزین این حالت ساده می‌شود
 * تا مسیرهای شرکت/مشتری/تجهیزات/پیش‌فاکتور به آن اضافه شوند.
 */
@Composable
private fun AppRoot() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
    } else {
        HomeScreen()
    }
}
