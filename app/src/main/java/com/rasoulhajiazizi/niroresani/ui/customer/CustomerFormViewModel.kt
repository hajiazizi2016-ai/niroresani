package com.rasoulhajiazizi.niroresani.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerTitleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerFormUiState(
    val customerId: Long? = null,
    val titleType: String = CustomerTitleType.MR,
    val firstName: String = "",
    val lastName: String = "",
    val planSubject: String = "",
    val planCode: String = "",
    val phone: String = "",
    val address: String = "",
    val description: String = "",
    val originalCreatedAt: Long = System.currentTimeMillis(),
    val firstNameError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    // اسنپ‌شات آخرین وضعیت ذخیره‌شده - برای تشخیص «تغییر ذخیره‌نشده» (بخش ۱۷ سند)
    val lastSavedTitleType: String = CustomerTitleType.MR,
    val lastSavedFirstName: String = "",
    val lastSavedLastName: String = "",
    val lastSavedPlanSubject: String = "",
    val lastSavedPlanCode: String = "",
    val lastSavedPhone: String = "",
    val lastSavedAddress: String = "",
    val lastSavedDescription: String = ""
) {
    val hasUnsavedChanges: Boolean
        get() = titleType != lastSavedTitleType ||
            firstName != lastSavedFirstName ||
            lastName != lastSavedLastName ||
            planSubject != lastSavedPlanSubject ||
            planCode != lastSavedPlanCode ||
            phone != lastSavedPhone ||
            address != lastSavedAddress ||
            description != lastSavedDescription
}

@HiltViewModel
class CustomerFormViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerFormUiState())
    val uiState: StateFlow<CustomerFormUiState> = _uiState.asStateFlow()

    init {
        val customerId = savedStateHandle.get<String>("customerId")?.toLongOrNull()
        if (customerId != null) loadCustomer(customerId)
    }

    private fun loadCustomer(id: Long) {
        viewModelScope.launch {
            customerDao.getById(id)?.let { customer ->
                _uiState.value = CustomerFormUiState(
                    customerId = customer.id,
                    titleType = customer.titleType,
                    firstName = customer.firstName,
                    lastName = customer.lastName,
                    planSubject = customer.planSubject ?: "",
                    planCode = customer.planCode ?: "",
                    phone = customer.phone ?: "",
                    address = customer.address,
                    description = customer.description ?: "",
                    originalCreatedAt = customer.createdAt,
                    lastSavedTitleType = customer.titleType,
                    lastSavedFirstName = customer.firstName,
                    lastSavedLastName = customer.lastName,
                    lastSavedPlanSubject = customer.planSubject ?: "",
                    lastSavedPlanCode = customer.planCode ?: "",
                    lastSavedPhone = customer.phone ?: "",
                    lastSavedAddress = customer.address,
                    lastSavedDescription = customer.description ?: ""
                )
            }
        }
    }

    fun onTitleTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(titleType = value)
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, firstNameError = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onPlanSubjectChange(value: String) {
        _uiState.value = _uiState.value.copy(planSubject = value)
    }

    fun onPlanCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(planCode = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun save() {
        val state = _uiState.value
        val nameLabel = if (state.titleType == CustomerTitleType.COMPANY) "نام شرکت" else "نام مشتری"
        val validation = InputValidators.validateRequiredText(state.firstName, nameLabel)
        if (validation is InputValidators.ValidationResult.Invalid) {
            _uiState.value = state.copy(firstNameError = validation.message)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            if (state.customerId == null) {
                customerDao.insert(
                    CustomerEntity(
                        titleType = state.titleType,
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        planSubject = state.planSubject.trim().ifBlank { null },
                        planCode = state.planCode.trim().ifBlank { null },
                        phone = state.phone.trim().ifBlank { null },
                        address = state.address.trim(),
                        description = state.description.trim().ifBlank { null }
                    )
                )
            } else {
                customerDao.update(
                    CustomerEntity(
                        id = state.customerId,
                        titleType = state.titleType,
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        planSubject = state.planSubject.trim().ifBlank { null },
                        planCode = state.planCode.trim().ifBlank { null },
                        phone = state.phone.trim().ifBlank { null },
                        address = state.address.trim(),
                        description = state.description.trim().ifBlank { null },
                        createdAt = state.originalCreatedAt
                    )
                )
            }
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true,
                lastSavedTitleType = state.titleType,
                lastSavedFirstName = state.firstName,
                lastSavedLastName = state.lastName,
                lastSavedPlanSubject = state.planSubject,
                lastSavedPlanCode = state.planCode,
                lastSavedPhone = state.phone,
                lastSavedAddress = state.address,
                lastSavedDescription = state.description
            )
        }
    }
}
