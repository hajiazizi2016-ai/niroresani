package com.rasoulhajiazizi.niroresani.navigation

/**
 * تمام مسیرهای ناوبری برنامه در یک مکان مرکزی.
 * فازهای بعدی (تجهیزات، پیش‌فاکتور) مسیرهای خودشان را همینجا اضافه می‌کنند.
 */
object Routes {
    const val HOME = "home"
    const val COMPANY = "company"
    const val CUSTOMER_LIST = "customer_list"
    const val CUSTOMER_FORM = "customer_form"
    const val CUSTOMER_FORM_WITH_ID = "customer_form?customerId={customerId}"
    const val CATALOG_ROOT = "catalog?parentId={parentId}&title={title}"
    const val COMING_SOON = "coming_soon/{title}"

    fun customerFormRoute(customerId: Long? = null): String =
        if (customerId == null) "customer_form" else "customer_form?customerId=$customerId"

    fun catalogRoute(parentId: Long? = null, title: String = "بانک تجهیزات"): String {
        val idPart = parentId?.toString() ?: ""
        return "catalog?parentId=$idPart&title=$title"
    }

    fun comingSoonRoute(title: String): String = "coming_soon/$title"
}
