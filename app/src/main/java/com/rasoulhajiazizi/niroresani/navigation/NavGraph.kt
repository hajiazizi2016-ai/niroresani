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
import com.rasoulhajiazizi.niroresani.ui.backup.BackupScreen
import com.rasoulhajiazizi.niroresani.ui.quotation.QuotationReviewScreen
import com.rasoulhajiazizi.niroresani.ui.security.SecurityScreen
import com.rasoulhajiazizi.niroresani.ui.settings.SettingsScreen

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
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onContactDeveloperClick = { navController.navigate(Routes.comingSoonRoute("ارتباط با سازنده")) },
                onSearchClick = { navController.navigate(Routes.QUOTATION_LIST) },
                onNewQuotationClick = { navController.navigate(Routes.QUOTATION_CUSTOMER_PICKER) }
            )
        }

        composable(Routes.COMPANY) {
            CompanyScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onCompanyClick = { navController.navigate(Routes.COMPANY) },
                onSecurityClick = { navController.navigate(Routes.SECURITY) },
                onBackupClick = { navController.navigate(Routes.BACKUP) }
            )
        }

        composable(Routes.SECURITY) {
            SecurityScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.BACKUP) {
            BackupScreen(onBack = { navController.popBackStack() })
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
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) {
            CustomerFormScreen(onBack = { navController.popBackStack() })
        }

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

        composable(Routes.QUOTATION_CUSTOMER_PICKER) {
            QuotationCustomerPickerScreen(
                onBack = { navController.popBackStack() },
                onAddCustomerClick = { navController.navigate(Routes.customerFormRoute()) },
                onCustomerSelected = { _, _ ->
                    navController.navigate(Routes.quotationCatalogRoute(null, "انتخاب اقلام"))
                }
            )
        }

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

       composable(Routes.QUOTATION_REVIEW) {
    QuotationReviewScreen(
        onBack = { navController.popBackStack() },
        onDiscardDraft = {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        },
        onSaved = { quotationId, wasEditing ->
            ...
        }
    )
}

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
            QuotationDetailScreen(
                onBack = { navController.popBackStack() },
                onEditClick = { navController.navigate(Routes.quotationCatalogRoute(null, "ویرایش اقلام")) }
            )
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
