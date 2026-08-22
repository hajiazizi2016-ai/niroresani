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
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savedQuotationId: Long? = null,
    val errorMessage: String? = null
)

/**
 * صفحه بازبینی نهایی. یک ViewModel هم برای ایجاد پیش‌فاکتور جدید (فاز ۴) و هم
 * برای ذخیره ویرایش پیش‌فاکتور موجود (فاز ۵) استفاده می‌شود؛ تفکیک بر اساس
 * QuotationDraftStore.state.editingQuotationId انجام می‌شود.
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
                    totalAmount = draft.totalAmount,
                    isEditing = draft.isEditing
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

            val quotationId: Long
            if (draft.isEditing) {
                quotationId = draft.editingQuotationId!!
                val customerSnapshot = draft.originalCustomerSnapshotJson ?: buildCustomerSnapshot(draft.customerId)
                quotationItemDao.deleteAllForQuotation(quotationId)
                quotationDao.update(
                    QuotationEntity(
                        id = quotationId,
                        number = draft.originalNumber ?: "",
                        issueDateShamsi = draft.originalIssueDateShamsi ?: PersianDateFormatter.formatFull(now),
                        issueDateEpoch = draft.originalIssueDateEpoch ?: now,
                        customerId = draft.customerId,
                        companySnapshotJson = draft.originalCompanySnapshotJson ?: buildCompanySnapshot(),
                        customerSnapshotJson = customerSnapshot,
                        description = draft.description.ifBlank { null },
                        totalAmount = draft.totalAmount,
                        status = "FINAL",
                        createdAt = draft.originalIssueDateEpoch ?: now,
                        updatedAt = now
                    )
                )
            } else {
                val shamsiYear = PersianDateFormatter.currentShamsiYear(now)
                val yearSuffix = "%-$shamsiYear"
                val countThisYear = quotationDao.countForYear(yearSuffix)
                val quotationNumber = "${countThisYear + 1}-$shamsiYear"

                quotationId = quotationDao.insert(
                    QuotationEntity(
                        number = quotationNumber,
                        issueDateShamsi = PersianDateFormatter.formatFull(now),
                        issueDateEpoch = now,
                        customerId = draft.customerId,
                        companySnapshotJson = buildCompanySnapshot(),
                        customerSnapshotJson = buildCustomerSnapshot(draft.customerId),
                        description = draft.description.ifBlank { null },
                        totalAmount = draft.totalAmount,
                        status = "FINAL",
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }

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
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true, savedQuotationId = quotationId)
        }
    }

    private suspend fun buildCompanySnapshot(): String {
        val company = companyDao.getCompany()
        return JSONObject().apply {
            put("name", company?.name ?: "")
            put("registrationNumber", company?.registrationNumber ?: "")
            put("logoPath", company?.logoPath ?: "")
        }.toString()
    }

    private suspend fun buildCustomerSnapshot(customerId: Long): String {
        val customer = customerDao.getById(customerId)
        return JSONObject().apply {
            put("firstName", customer?.firstName ?: "")
            put("lastName", customer?.lastName ?: "")
            put("address", customer?.address ?: "")
        }.toString()
    }
}
