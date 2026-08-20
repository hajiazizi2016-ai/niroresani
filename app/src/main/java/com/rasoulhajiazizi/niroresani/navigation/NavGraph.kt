package com.rasoulhajiazizi.niroresani.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rasoulhajiazizi.niroresani.ui.catalog.CatalogMode
import com.rasoulhajiazizi.niroresani.ui.catalog.CatalogScreen
import com.rasoulhajiazizi.niroresani.ui.common.ComingSoonScreen
import com.rasoulhajiazizi.niroresani.ui.company.CompanyScreen
import com.rasoulhajiazizi.niroresani.ui.customer.CustomerFormScreen
import com.rasoulhajiazizi.niroresani.ui.customer.CustomerListScreen
import com.rasoulhajiazizi.niroresani.ui.home.HomeScreen
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationCustomerPickerScreen
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationDetailScreen
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationListScreen
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationReviewScreen

/**
 * ریشه ناوبری برنامه. از فاز ۴ به بعد، مسیر کامل ایجاد پیش‌فاکتور برقرار است:
 * صفحه اصلی → انتخاب مشتری → انتخاب اقلام (درخت تجهیزات در حالت انتخاب) →
 * بازبینی نهایی → ذخیره → بازگشت به صفحه اصلی.
 */
@Composable
fun NiroResaniNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onMenuItemClick = { title ->
                    when (title) {
                        "شرکت" -> navController.navigate(Routes.COMPANY)
                        "مشتری" -> navController.navigate(Routes.CUSTOMER_LIST)
                        "تجهیزات" -> navController.navigate(Routes.catalogRoute(null, "بانک تجهیزات"))
                        "پیش‌فاکتور" -> navController.navigate(Routes.QUOTATION_LIST)
                        else -> navController.navigate(Routes.comingSoonRoute(title))
                    }
                },
                onSettingsClick = { navController.navigate(Routes.comingSoonRoute("تنظیمات")) },
                onContactDeveloperClick = { navController.navigate(Routes.comingSoonRoute("ارتباط با سازنده")) },
                onSearchClick = { navController.navigate(Routes.QUOTATION_LIST) },
                onNewQuotationClick = { navController.navigate(Routes.QUOTATION_CUSTOMER_PICKER) }
            )
        }

        composable(Routes.COMPANY) {
            CompanyScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CUSTOMER_LIST) {
            CustomerListScreen(
                onBack = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.customerFormRoute()) },
                onCustomerClick = { id -> navController.navigate(Routes.customerFormRoute(id)) }
            )
        }

        composable(
            route = Routes.CUSTOMER_FORM_WITH_ID,
            arguments = listOf(navArgument("customerId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CustomerFormScreen(onBack = { navController.popBackStack() })
        }

        // بانک تجهیزات - حالت مرور عادی (از صفحه اصلی)
        composable(
            route = Routes.CATALOG_ROOT,
            arguments = listOf(
                navArgument("parentId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("title") { type = NavType.StringType; defaultValue = "بانک تجهیزات" }
            )
        ) {
            CatalogScreen(
                mode = CatalogMode.BROWSE,
                onBack = { navController.popBackStack() },
                onCategoryClick = { childId, childTitle ->
                    navController.navigate(Routes.catalogRoute(childId, childTitle))
                }
            )
        }

        // مرحله ۱ ایجاد پیش‌فاکتور: انتخاب مشتری
        composable(Routes.QUOTATION_CUSTOMER_PICKER) {
            QuotationCustomerPickerScreen(
                onBack = { navController.popBackStack() },
                onAddCustomerClick = { navController.navigate(Routes.customerFormRoute()) },
                onCustomerSelected = { _, _ ->
                    navController.navigate(Routes.quotationCatalogRoute(null, "انتخاب اقلام"))
                }
            )
        }

        // مرحله ۲: انتخاب اقلام از بانک تجهیزات (حالت انتخاب برای پیش‌فاکتور)
        composable(
            route = Routes.QUOTATION_CATALOG,
            arguments = listOf(
                navArgument("parentId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("title") { type = NavType.StringType; defaultValue = "انتخاب اقلام" }
            )
        ) {
            CatalogScreen(
                mode = CatalogMode.SELECT_FOR_QUOTATION,
                onBack = { navController.popBackStack() },
                onCategoryClick = { childId, childTitle ->
                    navController.navigate(Routes.quotationCatalogRoute(childId, childTitle))
                },
                onReviewQuotationClick = { navController.navigate(Routes.QUOTATION_REVIEW) }
            )
        }

        // مرحله ۳: بازبینی نهایی و ذخیره
        composable(Routes.QUOTATION_REVIEW) {
            QuotationReviewScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // لیست و جستجوی پیش‌فاکتورهای ذخیره‌شده
        composable(Routes.QUOTATION_LIST) {
            QuotationListScreen(
                onBack = { navController.popBackStack() },
                onQuotationClick = { id -> navController.navigate(Routes.quotationDetailRoute(id)) }
            )
        }

        composable(
            route = Routes.QUOTATION_DETAIL,
            arguments = listOf(navArgument("quotationId") { type = NavType.StringType })
        ) {
            QuotationDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.COMING_SOON,
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ComingSoonScreen(title = title, onBack = { navController.popBackStack() })
        }
    }
}
