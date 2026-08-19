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
import com.rasoulhajiazizi.niroresani.navigation.NiroResaniNavGraph
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
 * ریشه برنامه: ابتدا Splash (۳ ثانیه)، سپس گراف کامل ناوبری
 * (صفحه اصلی → شرکت / مشتری / سایر بخش‌ها).
 */
@Composable
private fun AppRoot() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
    } else {
        NiroResaniNavGraph()
    }
}
