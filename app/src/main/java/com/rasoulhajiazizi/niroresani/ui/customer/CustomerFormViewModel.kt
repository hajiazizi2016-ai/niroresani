package com.rasoulhajiazizi.niroresani.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerFormUiState(
    val customerId: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val description: String = "",
    val originalCreatedAt: Long = System.currentTimeMillis(),
    val firstNameError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    // اسنپ‌شات آخرین وضعیت ذخیره‌شده - برای تشخیص «تغییر ذخیره‌نشده» (بخش ۱۷ سند)
    val lastSavedFirstName: String = "",
    val lastSavedLastName: String = "",
    val lastSavedAddress: String = "",
    val lastSavedDescription: String = ""
) {
    val hasUnsavedChanges: Boolean
        get() = firstName != lastSavedFirstName ||
            lastName != lastSavedLastName ||
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
                    firstName = customer.firstName,
                    lastName = customer.lastName,
                    address = customer.address,
                    description = customer.description ?: "",
                    originalCreatedAt = customer.createdAt,
                    lastSavedFirstName = customer.firstName,
                    lastSavedLastName = customer.lastName,
                    lastSavedAddress = customer.address,
                    lastSavedDescription = customer.description ?: ""
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, firstNameError = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun save() {
        val state = _uiState.value
        val validation = InputValidators.validateRequiredText(state.firstName, "نام مشتری")
        if (validation is InputValidators.ValidationResult.Invalid) {
            _uiState.value = state.copy(firstNameError = validation.message)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            if (state.customerId == null) {
                customerDao.insert(
                    CustomerEntity(
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        address = state.address.trim(),
                        description = state.description.trim().ifBlank { null }
                    )
                )
            } else {
                customerDao.update(
                    CustomerEntity(
                        id = state.customerId,
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        address = state.address.trim(),
                        description = state.description.trim().ifBlank { null },
                        createdAt = state.originalCreatedAt
                    )
                )
            }
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true,
                lastSavedFirstName = state.firstName,
                lastSavedLastName = state.lastName,
                lastSavedAddress = state.address,
                lastSavedDescription = state.description
            )
        }
    }
}
