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
    val nationalId: String = "",
    val economicCode: String = "",
    val postalCode: String = "",
    val mobile: String = "",
    val fax: String = "",
    val address: String = "",
    val logoPath: String? = null,
    val signaturePath: String? = null,
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
    // اسنپ‌شات آخرین وضعیت ذخیره‌شده - برای تشخیص «تغییر ذخیره‌نشده» (بخش ۱۷ سند)
    val lastSavedName: String = "",
    val lastSavedRegistrationNumber: String = "",
    val lastSavedNationalId: String = "",
    val lastSavedEconomicCode: String = "",
    val lastSavedPostalCode: String = "",
    val lastSavedMobile: String = "",
    val lastSavedFax: String = "",
    val lastSavedAddress: String = "",
    val lastSavedLogoPath: String? = null,
    val lastSavedSignaturePath: String? = null
) {
    val hasUnsavedChanges: Boolean
        get() = name != lastSavedName ||
            registrationNumber != lastSavedRegistrationNumber ||
            nationalId != lastSavedNationalId ||
            economicCode != lastSavedEconomicCode ||
            postalCode != lastSavedPostalCode ||
            mobile != lastSavedMobile ||
            fax != lastSavedFax ||
            address != lastSavedAddress ||
            logoPath != lastSavedLogoPath ||
            signaturePath != lastSavedSignaturePath
}

/**
 * مدیریت اطلاعات شرکت. طبق الزام سند (بخش ۸، ۷۱، ۳۳۷، ۳۳۸):
 * اطلاعات شرکت یک‌بار ثبت و همیشه برای پیش‌فاکتورهای جدید استفاده می‌شود؛
 * اسناد قبلی (از طریق Snapshot در Quotation) تحت‌تاثیر تغییرات بعدی قرار نمی‌گیرند.
 */
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
                    nationalId = existing.nationalId ?: "",
                    economicCode = existing.economicCode ?: "",
                    postalCode = existing.postalCode ?: "",
                    mobile = existing.mobile ?: "",
                    fax = existing.fax ?: "",
                    address = existing.address ?: "",
                    logoPath = existing.logoPath,
                    signaturePath = existing.signaturePath,
                    lastSavedName = existing.name,
                    lastSavedRegistrationNumber = existing.registrationNumber,
                    lastSavedNationalId = existing.nationalId ?: "",
                    lastSavedEconomicCode = existing.economicCode ?: "",
                    lastSavedPostalCode = existing.postalCode ?: "",
                    lastSavedMobile = existing.mobile ?: "",
                    lastSavedFax = existing.fax ?: "",
                    lastSavedAddress = existing.address ?: "",
                    lastSavedLogoPath = existing.logoPath,
                    lastSavedSignaturePath = existing.signaturePath
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

    fun onNationalIdChange(value: String) {
        _uiState.value = _uiState.value.copy(nationalId = value, savedMessage = null)
    }

    fun onEconomicCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(economicCode = value, savedMessage = null)
    }

    fun onPostalCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(postalCode = value, savedMessage = null)
    }

    fun onMobileChange(value: String) {
        _uiState.value = _uiState.value.copy(mobile = value, savedMessage = null)
    }

    fun onFaxChange(value: String) {
        _uiState.value = _uiState.value.copy(fax = value, savedMessage = null)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value, savedMessage = null)
    }

    fun onLogoPicked(path: String) {
        _uiState.value = _uiState.value.copy(logoPath = path, savedMessage = null)
    }

    fun onLogoRemoved() {
        _uiState.value = _uiState.value.copy(logoPath = null, savedMessage = null)
    }

    fun onSignaturePicked(path: String) {
        _uiState.value = _uiState.value.copy(signaturePath = path, savedMessage = null)
    }

    fun onSignatureRemoved() {
        _uiState.value = _uiState.value.copy(signaturePath = null, savedMessage = null)
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
                nationalId = state.nationalId.trim().ifBlank { null },
                economicCode = state.economicCode.trim().ifBlank { null },
                postalCode = state.postalCode.trim().ifBlank { null },
                mobile = state.mobile.trim().ifBlank { null },
                fax = state.fax.trim().ifBlank { null },
                address = state.address.trim().ifBlank { null },
                logoPath = state.logoPath,
                signaturePath = state.signaturePath,
                createdAt = now,
                updatedAt = now
            )
            val newId = companyDao.upsert(entity)
            _uiState.value = _uiState.value.copy(
                companyId = if (state.companyId == null) newId else state.companyId,
                isSaving = false,
                savedMessage = "اطلاعات شرکت با موفقیت ذخیره شد",
                lastSavedName = entity.name,
                lastSavedRegistrationNumber = entity.registrationNumber,
                lastSavedNationalId = entity.nationalId ?: "",
                lastSavedEconomicCode = entity.economicCode ?: "",
                lastSavedPostalCode = entity.postalCode ?: "",
                lastSavedMobile = entity.mobile ?: "",
                lastSavedFax = entity.fax ?: "",
                lastSavedAddress = entity.address ?: "",
                lastSavedLogoPath = entity.logoPath,
                lastSavedSignaturePath = entity.signaturePath
            )
        }
    }
}
