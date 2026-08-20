package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.database.dao.QuotationDao
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class QuotationListUiState(
    val query: String = "",
    val quotations: List<QuotationEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuotationListViewModel @Inject constructor(
    private val quotationDao: QuotationDao
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<QuotationListUiState> = queryFlow
        .flatMapLatest { query ->
            val source = if (query.isBlank()) quotationDao.observeAll() else quotationDao.search(query)
            source.map { list -> QuotationListUiState(query = query, quotations = list) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuotationListUiState())

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }
}
