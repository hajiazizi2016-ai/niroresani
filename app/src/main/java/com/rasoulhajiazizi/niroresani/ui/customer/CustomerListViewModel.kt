package com.rasoulhajiazizi.niroresani.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerListUiState(
    val query: String = "",
    val customers: List<CustomerEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerDao: CustomerDao
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<CustomerListUiState> = queryFlow
        .flatMapLatest { query ->
            val source = if (query.isBlank()) customerDao.observeAll() else customerDao.search(query)
            source.map { list -> CustomerListUiState(query = query, customers = list) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CustomerListUiState())

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            customerDao.delete(customer)
        }
    }
}
