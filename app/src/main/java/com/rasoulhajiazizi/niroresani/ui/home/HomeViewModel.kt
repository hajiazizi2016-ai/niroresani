package com.rasoulhajiazizi.niroresani.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.PersianDateFormatter
import com.rasoulhajiazizi.niroresani.core.database.AppDatabase
import com.rasoulhajiazizi.niroresani.core.database.SeedData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentDateShamsi: String = "",
    val isSeeding: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            currentDateShamsi = PersianDateFormatter.formatFull(System.currentTimeMillis())
        )
        seedDatabaseIfNeeded()
    }

    /**
     * درج داده اولیه بانک تجهیزات در اولین اجرای برنامه (فاز ۱ نقشه راه).
     * SeedData.populate خودش بررسی می‌کند که قبلاً داده وارد نشده باشد.
     */
    private fun seedDatabaseIfNeeded() {
        viewModelScope.launch {
            SeedData.populate(database)
            _uiState.value = _uiState.value.copy(isSeeding = false)
        }
    }
}
