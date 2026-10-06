package com.rasoulhajiazizi.niroresani.core.database

import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.SettingsEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.UnitEntity

object SeedData {

    /**
     * شماره نسخه‌ی ساختار بانک تجهیزات. هر بار که درخت دسته‌ها/آیتم‌ها (rootCategories)
     * تغییر می‌کند، این عدد باید افزایش یابد تا populate() بانک تجهیزات قدیمی را حذف و
     * نسخه‌ی جدید را جایگزین کند. این کار هرگز روی پیش‌فاکتورهای ثبت‌شده‌ی قبلی کاربر اثر
     * نمی‌گذارد چون آن‌ها Snapshot مستقل از بانک تجهیزات هستند.
     */
    private const val CATALOG_SEED_VERSION = "2"
    private const val SETTINGS_KEY_CATALOG_SEED_VERSION = "catalog_seed_version"

    val units = listOf("عدد", "متر", "کیلوگرم", "تن", "سرویس", "ساعت", "روز")

    data class SeedCategory(
        val title: String,
        val children: List<SeedCategory> = emptyList(),
        val items: List<String> = emptyList(),
        val defaultUnit: String = "عدد"
    )

    val rootCategories: List<SeedCategory> = listOf(
        SeedCategory(
            title = "خط و پست ۲۰ کیلوولت هوایی",
            items = listOf(
                "کات اوت",
                "برقگیر پلیمری"
            ),
            children = listOf(
                SeedCategory(
                    title = "تیر بتنی",
                    items = listOf(
                        "تیر ۱۵/۸۰۰", "تیر ۱۵/۶۰۰", "تیر ۱۵/۴۰۰",
                        "تیر ۱۲/۸۰۰", "تیر ۱۲/۶۰۰", "تیر ۱۲/۴۰۰",
                        "تیر ۹/۶۰۰", "تیر ۹/۴۰۰", "تیر ۹/۲۰۰"
                    )
                ),
                SeedCategory(
                    title = "کراس آرم",
                    items = listOf(
                        "۳متری نمره ۸", "۳ متری نمره ۷", "۲/۴۴ نمره ۸", "۲/۴۴ نمره ۷",
                        "۲متری نمره ۸", "۲متری نمره ۷", "۱/۵ متری نمره ۸", "۱/۵ متری نمره ۷",
                        "تسمه حایل ۷۰/۵/۵", "پشتبند تابلو"
                    )
                ),
                SeedCategory(
                    title = "پیچ و مهره",
                    items = listOf(
                        "پیچ و مهره ۴سانت", "پیچ عدسی ۴سانت",
                        "دوسر دنده ۶۰۰", "دوسردنده ۵۵۰", "دوسردنده ۵۰۰", "دوسردنده ۴۵۰",
                        "دوسردنده ۴۰۰", "دو سردنده ۳۵۰", "دوسر دنده ۳۰۰", "دو سردنده ۲۵۰",
                        "تک سر دنده ۴۰۰", "تک سر دنده ۳۵۰", "تک سر دنده ۳۰۰", "تک سر دنده ۲۵۰"
                    )
                ),
                SeedCategory(
                    title = "مقره و لوازم مربوطه",
                    items = listOf(
                        "مهره چشمی", "شیگل و پین و اپلیت", "مقره کششی پلیمری", "گیره انتهایی",
                        "سیم‌گیر ۷۰", "مقره سوزنی پلیمری", "مقره سوزنی سرامیکی",
                        "راس تیر ناودانی بلند", "پین کناری بلند"
                    )
                ),
                SeedCategory(
                    title = "ترانسفورماتور",
                    items = listOf(
                        "تکفاز ۱۰kva", "تکفاز ۱۵kva", "تک فاز ۲۵kva",
                        "سه فاز ۲۵kva", "سه فاز ۵۰kva", "سه فاز ۷۵kva", "سه فاز ۱۰۰kva",
                        "سه فاز ۱۶۰kva", "سه فاز ۲۰۰kva", "سه فاز ۲۵۰kva", "سه فاز ۳۱۵kva",
                        "سه فاز ۴۰۰kva"
                    )
                ),
                SeedCategory(
                    title = "کابل‌ها",
                    defaultUnit = "متر",
                    items = listOf(
                        "آلومینیومی ۱۲۰+۲۴۰*۳", "آلومینیومی ۹۵+۱۸۵*۳", "آلومینیومی ۵۰+۹۵*۳",
                        "آلومینیومی ۲۵+۵۰*۳", "آلومینیومی ۱۶*۴", "کابل فولادی ۵۰",
                        "کابل مسی ۵۰ مسی", "هادی روکش دار مینک"
                    )
                ),
                SeedCategory(
                    title = "کابلشو",
                    items = listOf(
                        "بی متال ۲۴۰", "بی متال ۱۸۵", "بی متال ۱۲۰", "بی متال ۹۵",
                        "بی متال ۷۰", "بی متال ۵۰", "بی متال ۳۵", "بی متال ۲۵", "بی متال ۱۶",
                        "پرسی ۵۰", "فول بیمتال ۷۰", "فول بیمتال ۵۰", "شیرینگ حرارتی"
                    )
                ),
                SeedCategory(
                    title = "کلمپ و گیره",
                    items = listOf(
                        "گیره هات لاین", "زین هات لاین", "رکاب هات لاین",
                        "کلمپ دو پیچه آلومینیومی", "کلمپ بی متال ۵۰", "کلمپ دوپیچه دوطرف دندانه دار"
                    )
                ),
                SeedCategory(
                    title = "کاور",
                    items = listOf(
                        "بوشینگ ترانس", "کات اوت", "برقگیر", "کلمپ",
                        "مقره سوزنی سرامیکی", "مقره سوزنی پلیمری", "کنسول با لوله ۴ اینچ"
                    )
                ),
                SeedCategory(
                    title = "سکوها",
                    items = listOf(
                        "کات اوت و برق گیر یک طرفه", "ترانس یک طرفه ۱۱۰ سانت",
                        "ترانس دو طرفه نمره ۸", "ترانس دوطرفه نمره ۱۰", "ترانس دوطرفه نمره ۱۲"
                    )
                ),
                SeedCategory(
                    title = "سیستم ارتینگ",
                    items = listOf(
                        "میله ارت دو متری", "میله ارت ۱/۵ متری", "کلمپ انگشتی میله ارت", "خاک بنتونیت"
                    )
                ),
                SeedCategory(
                    title = "لوله",
                    items = listOf(
                        "پلی اتیلن ۳ اینچ", "پلی اتیلن ۲ اینچ", "پلی اتیلن ۳/۴ اینچ"
                    )
                ),
                SeedCategory(
                    title = "تابلو",
                    items = listOf("تیپ یک", "تیپ ۲")
                ),
                SeedCategory(
                    title = "هزینه‌های اجرایی",
                    defaultUnit = "سرویس",
                    items = listOf(
                        "چاله کنی تیر", "چاله ارت و کانال", "سنگ لاشه", "سیمان", "شن و ماسه",
                        "بتن آماده", "کارگر روز کاری", "جوشکاری و تراشکاری",
                        "حمل و نقل وسایل و ترانس", "تریلی",
                        "جرثقیل (بارگیری/تخلیه/تیرگذاری/نصب ترانس)", "خاموشی شبکه", "دستمزد سیمبان"
                    )
                )
            )
        ),
        SeedCategory(
            title = "فشار ضعیف ۴۰۰ ولت"
        ),
        SeedCategory(
            title = "پست زمینی ۲۰ کیلوولت"
        )
    )

    suspend fun populate(database: AppDatabase) {
        val savedVersion = database.settingsDao().get(SETTINGS_KEY_CATALOG_SEED_VERSION)
        if (savedVersion == CATALOG_SEED_VERSION) return

        // بازسازی کامل بانک تجهیزات (دسته‌ها/واحدها/آیتم‌ها). این کار هرگز روی
        // پیش‌فاکتورهای ثبت‌شده‌ی قبلی اثر نمی‌گذارد چون تمام اطلاعات آن‌ها به‌صورت
        // Snapshot مستقل در جدول quotation_item نگهداری می‌شود.
        database.catalogItemDao().deleteAll()
        database.categoryDao().deleteAll()
        database.unitDao().deleteAll()

        val unitIdByName = mutableMapOf<String, Long>()
        units.forEach { unitName ->
            val id = database.unitDao().insert(UnitEntity(title = unitName))
            unitIdByName[unitName] = id
        }

        suspend fun insertCategoryTree(seed: SeedCategory, parentId: Long?, sortOrder: Int) {
            val categoryId = database.categoryDao().insert(
                CategoryEntity(title = seed.title, parentId = parentId, sortOrder = sortOrder)
            )
            seed.items.forEachIndexed { itemIndex, itemTitle ->
                val unitId = unitIdByName[seed.defaultUnit] ?: unitIdByName["عدد"]!!
                database.catalogItemDao().insert(
                    CatalogItemEntity(
                        categoryId = categoryId,
                        title = itemTitle,
                        unitId = unitId,
                        currentPrice = 0L,
                        sortOrder = itemIndex
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

        database.settingsDao().set(SettingsEntity(SETTINGS_KEY_CATALOG_SEED_VERSION, CATALOG_SEED_VERSION))
    }
}
