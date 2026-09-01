package com.rasoulhajiazizi.niroresani.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BackupUiState(
    val isWorking: Boolean = false,
    val errorMessage: String? = null,
    val showRestoreConfirm: Boolean = false
)

/**
 * ViewModel این صفحه بدون تزریق Context ساده نگه داشته شده؛ عملیات فایل واقعی
 * (که نیاز به Context دارد) در خود Composable با استفاده از BackupManager انجام می‌شود
 * و این کلاس فقط وضعیت UI (در حال کار بودن / خطا / دیالوگ تایید) را مدیریت می‌کند.
 */
class BackupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun onRestoreFileSelected() {
        _uiState.value = _uiState.value.copy(showRestoreConfirm = true)
    }

    fun dismissRestoreConfirm() {
        _uiState.value = _uiState.value.copy(showRestoreConfirm = false)
    }

    fun setWorking(working: Boolean) {
        _uiState.value = _uiState.value.copy(isWorking = working)
    }

    fun setError(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message, isWorking = false)
    }
}
