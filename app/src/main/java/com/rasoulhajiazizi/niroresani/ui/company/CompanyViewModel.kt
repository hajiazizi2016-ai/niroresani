package com.rasoulhajiazizi.niroresani.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.database.dao.CompanyDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CompanyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompanyUiState(
    val companyId: Long? = null,
    val name: String = "",
    val registrationNumber: String = "",
    val logoPath: String? = null,
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val savedMessage: String? = null
)

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val companyDao: CompanyDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyUiState())
    val uiState: StateFlow<CompanyUiState> = _uiState.asStateFlow()

    init {
        loadCompany()
    }

    private fun loadCompany() {
        viewModelScope.launch {
            val existing = companyDao.getCompany()
            if (existing != null) {
                _uiState.value = _uiState.value.copy(
                    companyId = existing.id,
                    name = existing.name,
                    registrationNumber = existing.registrationNumber,
                    logoPath = existing.logoPath
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null, savedMessage = null)
    }

    fun onRegistrationNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(registrationNumber = value, savedMessage = null)
    }

    fun onLogoPicked(path: String) {
        _uiState.value = _uiState.value.copy(logoPath = path, savedMessage = null)
    }

    fun onLogoRemoved() {
        _uiState.value = _uiState.value.copy(logoPath = null, savedMessage = null)
    }

    fun save() {
        val state = _uiState.value
        val nameValidation = InputValidators.validateRequiredText(state.name, "نام شرکت")
        if (nameValidation is InputValidators.ValidationResult.Invalid) {
            _uiState.value = state.copy(nameError = nameValidation.message)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val now = System.currentTimeMillis()
            val entity = CompanyEntity(
                id = state.companyId ?: 0L,
                name = state.name.trim(),
                registrationNumber = state.registrationNumber.trim(),
                logoPath = state.logoPath,
                createdAt = now,
                updatedAt = now
            )
            val newId = companyDao.upsert(entity)
            _uiState.value = _uiState.value.copy(
                companyId = if (state.companyId == null) newId else state.companyId,
                isSaving = false,
                savedMessage = "اطلاعات شرکت با موفقیت ذخیره شد"
            )
        }
    }
}
