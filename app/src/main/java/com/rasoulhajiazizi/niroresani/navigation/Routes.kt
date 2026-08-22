package com.rasoulhajiazizi.niroresani.navigation

object Routes {
    const val HOME = "home"
    const val COMPANY = "company"
    const val CUSTOMER_LIST = "customer_list"
    const val CUSTOMER_FORM = "customer_form"
    const val CUSTOMER_FORM_WITH_ID = "customer_form?customerId={customerId}"
    const val CATALOG_ROOT = "catalog?parentId={parentId}&title={title}"
    const val COMING_SOON = "coming_soon/{title}"

    const val QUOTATION_CUSTOMER_PICKER = "quotation_customer_picker"
    const val QUOTATION_CATALOG = "quotation_catalog?parentId={parentId}&title={title}"
    const val QUOTATION_REVIEW = "quotation_review"
    const val QUOTATION_LIST = "quotation_list"
    const val QUOTATION_DETAIL = "quotation_detail/{quotationId}"

    fun customerFormRoute(customerId: Long? = null): String =
        if (customerId == null) "customer_form" else "customer_form?customerId=$customerId"

    fun catalogRoute(parentId: Long? = null, title: String = "بانک تجهیزات"): String {
        val idPart = parentId?.toString() ?: ""
        return "catalog?parentId=$idPart&title=$title"
    }

    fun quotationCatalogRoute(parentId: Long? = null, title: String = "انتخاب اقلام"): String {
        val idPart = parentId?.toString() ?: ""
        return "quotation_catalog?parentId=$idPart&title=$title"
    }

    fun quotationDetailRoute(quotationId: Long): String = "quotation_detail/$quotationId"

    fun comingSoonRoute(title: String): String = "coming_soon/$title"
}
