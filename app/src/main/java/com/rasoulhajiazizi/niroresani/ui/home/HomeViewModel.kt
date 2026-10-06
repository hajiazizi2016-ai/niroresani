package com.rasoulhajiazizi.niroresani.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.PersianDateFormatter
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.AppDatabase
import com.rasoulhajiazizi.niroresani.core.database.SeedData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

data class HomeUiState(
    val currentDateShamsi: String = "",
    val currentTimeText: String = "",
    val isSeeding: Boolean = true
)

private val IRAN_TIME_ZONE = TimeZone.getTimeZone("Asia/Tehran")

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        startClock()
        seedDatabaseIfNeeded()
    }

    /**
     * ساعت زنده بر اساس ساعت رسمی ایران (Asia/Tehran) - بخش ۳ درخواست اصلاحات.
     * عمداً از TimeZone صریح استفاده می‌شود تا مستقل از تنظیمات منطقه‌زمانی گوشی باشد.
     */
    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val cal = Calendar.getInstance(IRAN_TIME_ZONE)
                cal.timeInMillis = now
                val hh = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
                val mm = cal.get(Calendar.MINUTE).toString().padStart(2, '0')
                val ss = cal.get(Calendar.SECOND).toString().padStart(2, '0')
                _uiState.value = _uiState.value.copy(
                    currentTimeText = PersianNumberFormatter.toPersianDigits("$hh:$mm:$ss"),
                    currentDateShamsi = PersianDateFormatter.formatFull(now)
                )
                delay(1000)
            }
        }
    }

    private fun seedDatabaseIfNeeded() {
        viewModelScope.launch {
            SeedData.populate(database)
            _uiState.value = _uiState.value.copy(isSeeding = false)
        }
    }
}
