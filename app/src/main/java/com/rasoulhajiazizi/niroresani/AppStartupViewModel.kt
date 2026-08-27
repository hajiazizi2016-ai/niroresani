package com.rasoulhajiazizi.niroresani

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.database.dao.SecurityDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppStartupUiState(
    val isLoading: Boolean = true,
    val isPasswordRequired: Boolean = false
)

@HiltViewModel
class AppStartupViewModel @Inject constructor(
    private val securityDao: SecurityDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStartupUiState())
    val uiState: StateFlow<AppStartupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val security = securityDao.get()
            val enabled = security?.isPasswordEnabled == true
            val hash = security?.passwordHash
            _uiState.value = AppStartupUiState(
                isLoading = false,
                isPasswordRequired = enabled && !hash.isNullOrBlank()
            )
        }
    }
}
