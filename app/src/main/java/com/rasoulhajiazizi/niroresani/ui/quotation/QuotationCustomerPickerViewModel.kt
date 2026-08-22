package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class QuotationCustomerPickerUiState(
    val query: String = "",
    val customers: List<CustomerEntity> = emptyList()
)

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
        // شروع «پیش‌فاکتور جدید» همیشه با سبد خالی است؛ پیش‌نویس ناتمام قبلی پاک می‌شود.
        // توجه: اگر مسیر ورود از «ویرایش پیش‌فاکتور» بود، این init فراخوانی نمی‌شود
        // چون آن مسیر مستقیماً به CatalogScreen می‌رود، نه این صفحه.
        draftStore.clear()
    }

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }

    fun selectCustomer(id: Long, label: String) {
        draftStore.setCustomer(id, label)
    }
}
