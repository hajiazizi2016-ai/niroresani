package com.rasoulhajiazizi.niroresani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rasoulhajiazizi.niroresani.navigation.NiroResaniNavGraph
import com.rasoulhajiazizi.niroresani.ui.security.LockScreen
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
 * توالی شروع برنامه: Splash → (در صورت فعال بودن رمز) LockScreen → NavGraph اصلی.
 */
@Composable
private fun AppRoot(startupViewModel: AppStartupViewModel = hiltViewModel()) {
    var showSplash by remember { mutableStateOf(true) }
    var isUnlocked by remember { mutableStateOf(false) }
    val startupState by startupViewModel.uiState.collectAsState()

    when {
        showSplash -> SplashScreen(onFinished = { showSplash = false })
        startupState.isLoading -> { /* صبر کوتاه برای بررسی وضعیت رمز - بدون UI اضافه */ }
        startupState.isPasswordRequired && !isUnlocked -> {
            LockScreen(onUnlocked = { isUnlocked = true })
        }
        else -> NiroResaniNavGraph()
    }
}
