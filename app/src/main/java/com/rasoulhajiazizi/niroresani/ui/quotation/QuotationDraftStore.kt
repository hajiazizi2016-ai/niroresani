package com.rasoulhajiazizi.niroresani.ui.quotation

import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class DraftItem(
    val catalogItemId: Long,
    val title: String,
    val unit: String,
    val unitPrice: Long,
    val quantity: Double
) {
    val lineTotal: Long get() = (quantity * unitPrice).toLong()
}

data class QuotationDraftState(
    val customerId: Long? = null,
    val customerLabel: String = "",
    val items: List<DraftItem> = emptyList(),
    val description: String = "",
    val editingQuotationId: Long? = null,
    val originalNumber: String? = null,
    val originalIssueDateShamsi: String? = null,
    val originalIssueDateEpoch: Long? = null,
    val originalCompanySnapshotJson: String? = null,
    val originalCustomerSnapshotJson: String? = null
) {
    val totalAmount: Long get() = items.sumOf { it.lineTotal }
    val isEditing: Boolean get() = editingQuotationId != null
}

@Singleton
class QuotationDraftStore @Inject constructor() {

    private val _state = MutableStateFlow(QuotationDraftState())
    val state: StateFlow<QuotationDraftState> = _state.asStateFlow()

    fun setCustomer(id: Long, label: String) {
        _state.value = _state.value.copy(customerId = id, customerLabel = label)
    }

    fun addItem(catalogItem: CatalogItemEntity, unitTitle: String, quantity: Double) {
        val existingIndex = _state.value.items.indexOfFirst { it.catalogItemId == catalogItem.id }
        val newItems = _state.value.items.toMutableList()
        if (existingIndex >= 0) {
            val existing = newItems[existingIndex]
            newItems[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            newItems.add(
                DraftItem(
                    catalogItemId = catalogItem.id,
                    title = catalogItem.title,
                    unit = unitTitle,
                    unitPrice = catalogItem.currentPrice,
                    quantity = quantity
                )
            )
        }
        _state.value = _state.value.copy(items = newItems)
    }

    fun updateQuantity(index: Int, newQuantity: Double) {
        val newItems = _state.value.items.toMutableList()
        if (index in newItems.indices) {
            newItems[index] = newItems[index].copy(quantity = newQuantity)
            _state.value = _state.value.copy(items = newItems)
        }
    }

    fun removeItem(index: Int) {
        val newItems = _state.value.items.toMutableList()
        if (index in newItems.indices) {
            newItems.removeAt(index)
            _state.value = _state.value.copy(items = newItems)
        }
    }

    fun setDescription(text: String) {
        _state.value = _state.value.copy(description = text)
    }

    fun loadForEditing(
        quotationId: Long,
        customerId: Long,
        customerLabel: String,
        items: List<DraftItem>,
        description: String,
        originalNumber: String,
        originalIssueDateShamsi: String,
        originalIssueDateEpoch: Long,
        originalCompanySnapshotJson: String,
        originalCustomerSnapshotJson: String
    ) {
        _state.value = QuotationDraftState(
            customerId = customerId,
            customerLabel = customerLabel,
            items = items,
            description = description,
            editingQuotationId = quotationId,
            originalNumber = originalNumber,
            originalIssueDateShamsi = originalIssueDateShamsi,
            originalIssueDateEpoch = originalIssueDateEpoch,
            originalCompanySnapshotJson = originalCompanySnapshotJson,
            originalCustomerSnapshotJson = originalCustomerSnapshotJson
        )
    }

    fun clear() {
        _state.value = QuotationDraftState()
    }
}
