package com.rasoulhajiazizi.niroresani.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.PasswordHasher
import com.rasoulhajiazizi.niroresani.core.database.dao.SecurityDao
import com.rasoulhajiazizi.niroresani.core.database.entity.SecurityEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecurityUiState(
    val isPasswordEnabled: Boolean = false,
    val hasExistingPassword: Boolean = false,
    val currentPasswordInput: String = "",
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * مدیریت رمز ورود اختیاری برنامه (بخش ۴۰۱، ۱۰۴۹، ۱۰۵۰ سند).
 */
@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityDao: SecurityDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val existing = securityDao.get()
            _uiState.value = _uiState.value.copy(
                isPasswordEnabled = existing?.isPasswordEnabled ?: false,
                hasExistingPassword = !existing?.passwordHash.isNullOrBlank()
            )
        }
    }

    fun onCurrentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPasswordInput = value, errorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPasswordInput = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordInput = value, errorMessage = null)
    }

    /** غیرفعال کردن کامل رمز - نیازمند تایید رمز فعلی برای جلوگیری از غیرفعال‌سازی غیرمجاز */
    fun disablePassword() {
        viewModelScope.launch {
            val existing = securityDao.get()
            if (existing?.passwordHash != null &&
                !PasswordHasher.verify(_uiState.value.currentPasswordInput, existing.passwordHash)
            ) {
                _uiState.value = _uiState.value.copy(errorMessage = "رمز فعلی صحیح نیست")
                return@launch
            }
            securityDao.upsert(SecurityEntity(id = 1, passwordHash = null, isPasswordEnabled = false))
            _uiState.value = _uiState.value.copy(
                isPasswordEnabled = false,
                hasExistingPassword = false,
                currentPasswordInput = "",
                successMessage = "رمز ورود غیرفعال شد"
            )
        }
    }

    /** ثبت رمز جدید یا تغییر رمز موجود */
    fun setOrChangePassword() {
        val state = _uiState.value

        if (state.hasExistingPassword) {
            viewModelScope.launch {
                val existing = securityDao.get()
                if (existing?.passwordHash != null &&
                    !PasswordHasher.verify(state.currentPasswordInput, existing.passwordHash)
                ) {
                    _uiState.value = state.copy(errorMessage = "رمز فعلی صحیح نیست")
                    return@launch
                }
                applyNewPassword(state)
            }
        } else {
            viewModelScope.launch { applyNewPassword(state) }
        }
    }

    private suspend fun applyNewPassword(state: SecurityUiState) {
        if (state.newPasswordInput.length < 4) {
            _uiState.value = state.copy(errorMessage = "رمز باید حداقل ۴ رقم/کاراکتر باشد")
            return
        }
        if (state.newPasswordInput != state.confirmPasswordInput) {
            _uiState.value = state.copy(errorMessage = "رمز و تکرار آن یکسان نیستند")
            return
        }
        val hash = PasswordHasher.hash(state.newPasswordInput)
        securityDao.upsert(SecurityEntity(id = 1, passwordHash = hash, isPasswordEnabled = true))
        _uiState.value = SecurityUiState(
            isPasswordEnabled = true,
            hasExistingPassword = true,
            successMessage = "رمز ورود با موفقیت ثبت شد"
        )
    }
}
