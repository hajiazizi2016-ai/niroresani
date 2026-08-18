package com.rasoulhajiazizi.niroresani.core.database

import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.UnitEntity

/**
 * داده اولیه بانک تجهیزات، دقیقاً طبق ساختار درختی مشخص‌شده در سند اصلی پروژه
 * (بخش‌های ۲۱ تا ۳۶ و ۵۴۵ تا ۵۷۰).
 *
 * این کلاس هنگام اولین اجرای برنامه توسط SeedDatabaseWorker (در ماژول app) فراخوانی می‌شود.
 * قیمت‌های اولیه صفر هستند تا کاربر پیش از هر برآورد، قیمت واقعی بازار را وارد کند
 * (اصل مهم سند: بخش ۲۱۳، ۸۸۶ - قیمت واقعی خرید، نه قیمت فرضی).
 */
object SeedData {

    // ---- واحدهای اندازه‌گیری ----
    val units = listOf("عدد", "متر", "کیلوگرم", "تن", "سرویس", "ساعت", "روز")

    data class SeedCategory(
        val title: String,
        val children: List<SeedCategory> = emptyList(),
        val items: List<String> = emptyList(), // نام آیتم‌های مستقیم این دسته
        val defaultUnit: String = "عدد"
    )

    /** ساختار کامل درخت دسته‌بندی و آیتم‌های اولیه */
    val rootCategories: List<SeedCategory> = listOf(
        SeedCategory(
            title = "خط هوایی",
            children = listOf(
                SeedCategory(
                    title = "فشار متوسط ۲۰ کیلوولت",
                    children = listOf(
                        SeedCategory(
                            title = "تیرهای بتنی",
                            items = listOf(
                                "تیر بتنی ۱۵/۸۰۰",
                                "تیر بتنی ۱۵/۶۰۰",
                                "تیر بتنی ۱۵/۴۰۰",
                                "تیر بتنی ۱۲/۸۰۰",
                                "تیر بتنی ۱۲/۶۰۰",
                                "تیر بتنی ۱۲/۴۰۰"
                            )
                        ),
                        SeedCategory(title = "کراس آرم‌ها", items = listOf("کراس آرم استاندارد")),
                        SeedCategory(title = "پیچ و مهره‌ها", items = listOf("پیچ و مهره استاندارد", "واشر")),
                        SeedCategory(title = "مقره‌ها", items = listOf("مقره سوزنی", "مقره آویزی")),
                        SeedCategory(title = "گیره‌ها", items = listOf("گیره انتهایی", "گیره اتصال")),
                        SeedCategory(title = "یراق‌آلات", items = listOf("یراق‌آلات استاندارد فشار متوسط")),
                        SeedCategory(title = "تجهیزات تکمیلی", items = listOf("تجهیزات تکمیلی شبکه فشار متوسط"))
                    )
                ),
                SeedCategory(
                    title = "فشار ضعیف ۴۰۰ ولت",
                    children = listOf(
                        SeedCategory(title = "تیرها", items = listOf("تیر بتنی فشار ضعیف")),
                        SeedCategory(title = "سیم‌ها", items = listOf("سیم مسی هوایی"), defaultUnit = "متر"),
                        SeedCategory(title = "کابل‌ها", items = listOf("کابل خودنگهدار"), defaultUnit = "متر"),
                        SeedCategory(title = "یراق‌آلات", items = listOf("یراق‌آلات فشار ضعیف")),
                        SeedCategory(title = "تجهیزات نصب", items = listOf("تجهیزات نصب فشار ضعیف"))
                    )
                )
            )
        ),
        SeedCategory(
            title = "پست",
            children = listOf(
                SeedCategory(
                    title = "پست هوایی تک پایه",
                    items = listOf(
                        "هات‌لاین",
                        "برقگیر پلیمری ۲۰ کیلوولت",
                        "کات اوت پلیمری",
                        "فیوز لینک",
                        "کابلشو",
                        "سکوی ترانس یکطرفه ۱۱۰ سانتی‌متر",
                        "ترانسفورماتور تک‌فاز",
                        "ترانسفورماتور سه‌فاز"
                    )
                ),
                SeedCategory(
                    title = "پست هوایی دو پایه",
                    items = listOf(
                        "هات‌لاین",
                        "برقگیر پلیمری ۲۰ کیلوولت",
                        "کات اوت پلیمری",
                        "فیوز لینک",
                        "کابلشو",
                        "سکوی ترانسفورماتور دو طرفه نمره ۸",
                        "سکوی ترانسفورماتور دو طرفه نمره ۱۰",
                        "سکوی ترانسفورماتور دو طرفه نمره ۱۲",
                        "سکوی ترانسفورماتور دو طرفه نمره ۱۴",
                        "ترانسفورماتور تک‌فاز",
                        "ترانسفورماتور سه‌فاز"
                    )
                )
            )
        ),
        SeedCategory(
            title = "مصالح",
            items = listOf("سنگ لاشه", "شن و ماسه", "سیمان", "بلوک سیمانی", "آجر"),
            defaultUnit = "تن"
        ),
        SeedCategory(
            title = "حمل و نقل",
            items = listOf("جرثقیل", "کرایه تریلی", "کرایه مصالح", "کرایه یراق‌آلات", "کرایه ترانس", "بیل مکانیکی"),
            defaultUnit = "سرویس"
        ),
        SeedCategory(
            title = "هزینه‌های کارگری",
            items = listOf("چاله‌کنی", "کارگر روز کاری", "دستمزد شیفته‌ریزی", "حفاری"),
            defaultUnit = "روز"
        ),
        SeedCategory(
            title = "سایر هزینه‌ها",
            items = listOf(
                "دستمزد سیمبان",
                "هزینه خاموشی شبکه",
                "هزینه جوشکاری و تراشکاری",
                "سایر هزینه‌های پیش‌بینی نشده"
            ),
            defaultUnit = "سرویس"
        ),
        SeedCategory(
            title = "هزینه‌های اجرایی و اداری شرکت",
            items = listOf("هزینه اجرایی و اداری شرکت"),
            defaultUnit = "سرویس"
        )
    )

    /**
     * درج بازگشتی درخت دسته‌بندی + آیتم‌ها در دیتابیس.
     * fراخوانی از SeedDatabaseWorker (ماژول app) در اولین اجرای برنامه.
     */
    suspend fun populate(database: AppDatabase) {
        if (database.catalogItemDao().count() > 0) return // فقط یک‌بار اجرا شود

        val unitIdByName = mutableMapOf<String, Long>()
        units.forEach { unitName ->
            val id = database.unitDao().insert(UnitEntity(title = unitName))
            unitIdByName[unitName] = id
        }

        suspend fun insertCategoryTree(seed: SeedCategory, parentId: Long?, sortOrder: Int) {
            val categoryId = database.categoryDao().insert(
                CategoryEntity(title = seed.title, parentId = parentId, sortOrder = sortOrder)
            )
            seed.items.forEachIndexed { _, itemTitle ->
                val unitId = unitIdByName[seed.defaultUnit] ?: unitIdByName["عدد"]!!
                database.catalogItemDao().insert(
                    CatalogItemEntity(
                        categoryId = categoryId,
                        title = itemTitle,
                        unitId = unitId,
                        currentPrice = 0L
                    )
                )
            }
            seed.children.forEachIndexed { idx, child ->
                insertCategoryTree(child, categoryId, idx)
            }
        }

        rootCategories.forEachIndexed { idx, root ->
            insertCategoryTree(root, null, idx)
        }
    }
}
