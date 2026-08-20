package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class QuotationCustomerPickerUiState(
    val query: String = "",
    val customers: List<com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity> = emptyList()
)

/**
 * صفحه انتخاب مشتری در شروع فرآیند ایجاد پیش‌فاکتور.
 * برخلاف CustomerListViewModel عمومی، این نسخه هنگام ورود، هر پیش‌نویس
 * ناتمام قبلی (مثلاً از یک تلاش لغوشده) را پاک می‌کند تا هر «پیش‌فاکتور جدید»
 * همیشه با سبد خالی شروع شود.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuotationCustomerPickerViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val draftStore: QuotationDraftStore
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<QuotationCustomerPickerUiState> = queryFlow
        .flatMapLatest { query ->
            val source = if (query.isBlank()) customerDao.observeAll() else customerDao.search(query)
            source.map { list -> QuotationCustomerPickerUiState(query = query, customers = list) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuotationCustomerPickerUiState())

    init {
        draftStore.clear()
    }

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }

    fun selectCustomer(id: Long, label: String) {
        draftStore.setCustomer(id, label)
    }
}
