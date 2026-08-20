package com.rasoulhajiazizi.niroresani.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.dao.CatalogItemDao
import com.rasoulhajiazizi.niroresani.core.database.dao.CategoryDao
import com.rasoulhajiazizi.niroresani.core.database.dao.PriceHistoryDao
import com.rasoulhajiazizi.niroresani.core.database.dao.UnitDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.PriceHistoryEntity
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationDraftStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val parentId: Long? = null,
    val screenTitle: String = "بانک تجهیزات",
    val query: String = "",
    val isSearching: Boolean = false,
    val childCategories: List<CategoryEntity> = emptyList(),
    val items: List<CatalogItemEntity> = emptyList(),
    val unitTitleById: Map<Long, String> = emptyMap(),
    val priceEditError: String? = null
)

/**
 * مدیریت نمایش درختی بانک تجهیزات (فاز ۳).
 * منطق: parentId == null یعنی سطح ریشه (خط هوایی، پست، مصالح، حمل و نقل، ...).
 * هر دسته می‌تواند هم زیرگروه داشته باشد و هم آیتم مستقیم (مثل «مصالح» که ریشه است
 * اما زیرگروه ندارد و مستقیماً آیتم دارد) - طبق ساختار seed سند اصلی.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val catalogItemDao: CatalogItemDao,
    private val unitDao: UnitDao,
    private val priceHistoryDao: PriceHistoryDao,
    private val draftStore: QuotationDraftStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val parentId: Long? = savedStateHandle.get<String>("parentId")?.toLongOrNull()
    private val initialTitle: String = savedStateHandle.get<String>("title") ?: "بانک تجهیزات"

    private val queryFlow = MutableStateFlow("")

    private val childrenFlow: Flow<List<CategoryEntity>> =
        if (parentId == null) categoryDao.observeRootCategories() else categoryDao.observeChildren(parentId)

    private val itemsFlow: Flow<List<CatalogItemEntity>> =
        if (parentId == null) flowOf(emptyList()) else catalogItemDao.observeByCategory(parentId)

    private val unitsFlow: Flow<Map<Long, String>> =
        unitDao.observeAll().map { units -> units.associate { it.id to it.title } }

    val uiState: StateFlow<CatalogUiState> = combine(
        queryFlow.flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else catalogItemDao.search(q)
        },
        childrenFlow,
        itemsFlow,
        unitsFlow,
        queryFlow
    ) { searchResults, children, items, units, query ->
        CatalogUiState(
            parentId = parentId,
            screenTitle = initialTitle,
            query = query,
            isSearching = query.isNotBlank(),
            childCategories = if (query.isBlank()) children else emptyList(),
            items = if (query.isBlank()) items else searchResults,
            unitTitleById = units
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogUiState())

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }

    /** تعداد اقلام فعلاً انتخاب‌شده در پیش‌نویس پیش‌فاکتور (برای نمایش نوار پایین در حالت انتخاب) */
    val draftItemCount: StateFlow<Int> = draftStore.state
        .map { it.items.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addItemToQuotation(item: CatalogItemEntity, quantity: Double) {
        val unitTitle = uiState.value.unitTitleById[item.unitId] ?: ""
        draftStore.addItem(item, unitTitle, quantity)
    }

    fun updatePrice(item: CatalogItemEntity, newPriceRaw: String, onDone: () -> Unit) {
        val validation = InputValidators.validatePrice(newPriceRaw)
        if (validation is InputValidators.ValidationResult.Invalid) {
            // خطا در قسمت UI dialog مدیریت می‌شود؛ اینجا صرفاً از ادامه جلوگیری می‌کنیم
            return
        }
        val newPrice = PersianNumberFormatter.toLatinDigits(newPriceRaw).trim().toLong()
        if (newPrice == item.currentPrice) {
            onDone()
            return
        }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            catalogItemDao.updatePrice(item.id, newPrice, now)
            // حفظ تاریخچه قیمت - الزام حیاتی سند (بخش ۸۹، ۳۶۰، ۸۹۰)
            priceHistoryDao.insert(
                PriceHistoryEntity(
                    catalogItemId = item.id,
                    oldPrice = item.currentPrice,
                    newPrice = newPrice,
                    changedAt = now
                )
            )
            onDone()
        }
    }
}
