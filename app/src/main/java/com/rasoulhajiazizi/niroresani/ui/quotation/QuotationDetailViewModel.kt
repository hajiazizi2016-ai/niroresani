package com.rasoulhajiazizi.niroresani.ui.quotation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class QuotationDetailUiState(
    val quotation: QuotationEntity? = null,
    val items: List<QuotationItemEntity> = emptyList(),
    val customerName: String = "",
    val customerAddress: String = "",
    val companyName: String = "",
    val companyRegistrationNumber: String = "",
    val companyLogoPath: String? = null
)

@HiltViewModel
class QuotationDetailViewModel @Inject constructor(
    private val quotationDao: QuotationDao,
    private val quotationItemDao: QuotationItemDao,
    private val draftStore: QuotationDraftStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotationDetailUiState())
    val uiState: StateFlow<QuotationDetailUiState> = _uiState.asStateFlow()

    init {
        val quotationId = savedStateHandle.get<String>("quotationId")?.toLongOrNull()
        if (quotationId != null) load(quotationId)
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            val quotation = quotationDao.getById(id) ?: return@launch
            val items = quotationItemDao.getForQuotation(id)

            var customerName = ""
            var customerAddress = ""
            var companyName = ""
            var companyRegistrationNumber = ""
            var companyLogoPath: String? = null
            try {
                val customerJson = JSONObject(quotation.customerSnapshotJson)
                customerName = "${customerJson.optString("firstName")} ${customerJson.optString("lastName")}"
                customerAddress = customerJson.optString("address")
                val companyJson = JSONObject(quotation.companySnapshotJson)
                companyName = companyJson.optString("name")
                companyRegistrationNumber = companyJson.optString("registrationNumber")
                companyLogoPath = companyJson.optString("logoPath").ifBlank { null }
            } catch (e: Exception) {
                // نادیده گرفتن خطای احتمالی پارس JSON قدیمی
            }

            _uiState.value = QuotationDetailUiState(
                quotation = quotation,
                items = items,
                customerName = customerName,
                customerAddress = customerAddress,
                companyName = companyName,
                companyRegistrationNumber = companyRegistrationNumber,
                companyLogoPath = companyLogoPath
            )
        }
    }

    /**
     * بارگذاری این پیش‌فاکتور در مخزن پیش‌نویس برای ویرایش (بخش ۷۸ سند).
     * شماره، تاریخ صدور و اسنپ‌شات شرکت اصلی حفظ می‌شوند.
     */
    fun startEdit() {
        val state = _uiState.value
        val quotation = state.quotation ?: return

        val draftItems = state.items.map {
            DraftItem(
                catalogItemId = it.catalogItemId,
                title = it.titleSnapshot,
                unit = it.unitSnapshot,
                unitPrice = it.unitPriceSnapshot,
                quantity = it.quantity
            )
        }

        draftStore.loadForEditing(
            quotationId = quotation.id,
            customerId = quotation.customerId,
            customerLabel = state.customerName,
            items = draftItems,
            description = quotation.description ?: "",
            originalNumber = quotation.number,
            originalIssueDateShamsi = quotation.issueDateShamsi,
            originalIssueDateEpoch = quotation.issueDateEpoch,
            originalCompanySnapshotJson = quotation.companySnapshotJson,
            originalCustomerSnapshotJson = quotation.customerSnapshotJson
        )
    }
}
