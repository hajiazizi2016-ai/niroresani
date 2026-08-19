package com.rasoulhajiazizi.niroresani.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rasoulhajiazizi.niroresani.ui.common.ComingSoonScreen
import com.rasoulhajiazizi.niroresani.ui.company.CompanyScreen
import com.rasoulhajiazizi.niroresani.ui.customer.CustomerFormScreen
import com.rasoulhajiazizi.niroresani.ui.customer.CustomerListScreen
import com.rasoulhajiazizi.niroresani.ui.home.HomeScreen

/**
 * ریشه ناوبری برنامه از فاز ۲ به بعد.
 * هر فاز جدید (تجهیزات در فاز ۳، پیش‌فاکتور در فاز ۴) مسیر خودش را
 * به‌جای ComingSoonScreen در این‌جا اضافه می‌کند، بدون نیاز به تغییر ساختار کلی.
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
                        else -> navController.navigate(Routes.comingSoonRoute(title))
                    }
                },
                onSettingsClick = { navController.navigate(Routes.comingSoonRoute("تنظیمات")) },
                onContactDeveloperClick = { navController.navigate(Routes.comingSoonRoute("ارتباط با سازنده")) },
                onSearchClick = { navController.navigate(Routes.comingSoonRoute("جستجوی پیش‌فاکتورها")) },
                onNewQuotationClick = { navController.navigate(Routes.comingSoonRoute("پیش‌فاکتور جدید")) }
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

        composable(
            route = Routes.COMING_SOON,
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ComingSoonScreen(title = title, onBack = { navController.popBackStack() })
        }
    }
}
