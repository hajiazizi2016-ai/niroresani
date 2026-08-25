package com.rasoulhajiazizi.niroresani.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.PasswordHasher
import com.rasoulhajiazizi.niroresani.core.database.dao.SecurityDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LockUiState(
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LockViewModel @Inject constructor(
    private val securityDao: SecurityDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    fun tryUnlock(password: String) {
        viewModelScope.launch {
            val security = securityDao.get()
            val hash = security?.passwordHash
            if (hash != null && PasswordHasher.verify(password, hash)) {
                _uiState.value = LockUiState(isUnlocked = true)
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = "رمز عبور اشتباه است")
            }
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}
