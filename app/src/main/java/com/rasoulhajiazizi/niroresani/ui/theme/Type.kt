package com.rasoulhajiazizi.niroresani.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * تایپوگرافی برنامه.
 *
 * ** توجه مهم - اقدام لازم قبل از اجرا **
 * در حال حاضر از فونت پیش‌فرض سیستم استفاده می‌شود تا پروژه بدون فایل اضافه
 * قابل اجراست. برای فارسی‌سازی کامل (طبق الزام سند)، فونت رایگان Vazirmatn را
 * از آدرس زیر دانلود و طبق راهنمای فایل SETUP.md نصب کنید:
 * https://github.com/rastikerdar/vazirmatn
 *
 * پس از افزودن فایل‌های فونت به res/font/، خط زیر را با
 * FontFamily(Font(R.font.vazirmatn_regular), Font(R.font.vazirmatn_bold, FontWeight.Bold))
 * جایگزین کنید.
 */
val AppFontFamily: FontFamily = FontFamily.Default

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

// فونت اختصاصی نمایش تاریخ در صفحه اصلی: «فونت شماره ۲ تیتر بولد» طبق بخش ۷ سند
val DateDisplayStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)
