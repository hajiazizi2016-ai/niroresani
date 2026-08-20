package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasoulhajiazizi.niroresani.core.common.InputValidators
import com.rasoulhajiazizi.niroresani.core.common.PersianDateFormatter
import com.rasoulhajiazizi.niroresani.core.common.PersianNumberFormatter
import com.rasoulhajiazizi.niroresani.core.database.dao.CompanyDao
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.dao.QuotationDao
import com.rasoulhajiazizi.niroresani.core.database.dao.QuotationItemDao
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class QuotationReviewUiState(
    val customerLabel: String = "",
    val items: List<DraftItem> = emptyList(),
    val description: String = "",
    val totalAmount: Long = 0L,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

/**
 * صفحه نهایی مسیر ایجاد پیش‌فاکتور (فاز ۴).
 * مسئولیت: نمایش سبد انتخاب‌شده، امکان اصلاح تعداد/حذف، و ذخیره نهایی با
 * Snapshot کامل اطلاعات شرکت و مشتری (الزام حیاتی سند - بخش ۷۸۶، ۲۰۱، ۳۷۰).
 */
@HiltViewModel
class QuotationReviewViewModel @Inject constructor(
    private val draftStore: QuotationDraftStore,
    private val companyDao: CompanyDao,
    private val customerDao: CustomerDao,
    private val quotationDao: QuotationDao,
    private val quotationItemDao: QuotationItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotationReviewUiState())
    val uiState: StateFlow<QuotationReviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            draftStore.state.collect { draft ->
                _uiState.value = _uiState.value.copy(
                    customerLabel = draft.customerLabel,
                    items = draft.items,
                    description = draft.description,
                    totalAmount = draft.totalAmount
                )
            }
        }
    }

    fun onQuantityChange(index: Int, rawValue: String) {
        val validation = InputValidators.validateQuantity(rawValue)
        if (validation is InputValidators.ValidationResult.Valid) {
            val normalized = PersianNumberFormatter.toLatinDigits(rawValue).trim().toDouble()
            draftStore.updateQuantity(index, normalized)
        }
    }

    fun onRemoveItem(index: Int) {
        draftStore.removeItem(index)
    }

    fun onDescriptionChange(text: String) {
        draftStore.setDescription(text)
    }

    fun save() {
        val draft = draftStore.state.value
        if (draft.customerId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "مشتری انتخاب نشده است")
            return
        }
        if (draft.items.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "حداقل یک قلم باید انتخاب شود")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            val now = System.currentTimeMillis()
            val shamsiYear = PersianDateFormatter.currentShamsiYear(now)
            val yearSuffix = "%-$shamsiYear"
            val countThisYear = quotationDao.countForYear(yearSuffix)
            val quotationNumber = "${countThisYear + 1}-$shamsiYear"

            val company = companyDao.getCompany()
            val customer = customerDao.getById(draft.customerId)

            val companySnapshot = JSONObject().apply {
                put("name", company?.name ?: "")
                put("registrationNumber", company?.registrationNumber ?: "")
                put("logoPath", company?.logoPath ?: "")
            }.toString()

            val customerSnapshot = JSONObject().apply {
                put("firstName", customer?.firstName ?: "")
                put("lastName", customer?.lastName ?: "")
                put("address", customer?.address ?: "")
            }.toString()

            val quotationId = quotationDao.insert(
                QuotationEntity(
                    number = quotationNumber,
                    issueDateShamsi = PersianDateFormatter.formatFull(now),
                    issueDateEpoch = now,
                    customerId = draft.customerId,
                    companySnapshotJson = companySnapshot,
                    customerSnapshotJson = customerSnapshot,
                    description = draft.description.ifBlank { null },
                    totalAmount = draft.totalAmount,
                    status = "FINAL",
                    createdAt = now,
                    updatedAt = now
                )
            )

            val items = draft.items.mapIndexed { index, item ->
                QuotationItemEntity(
                    quotationId = quotationId,
                    catalogItemId = item.catalogItemId,
                    titleSnapshot = item.title,
                    unitSnapshot = item.unit,
                    quantity = item.quantity,
                    unitPriceSnapshot = item.unitPrice,
                    lineTotal = item.lineTotal,
                    sortOrder = index
                )
            }
            quotationItemDao.insertAll(items)

            draftStore.clear()
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}
