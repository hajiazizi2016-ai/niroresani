package com.rasoulhajiazizi.niroresani.core.common

import java.util.Calendar
import java.util.TimeZone

/**
 * ابزار تبدیل تاریخ میلادی به شمسی (جلالی) بدون وابستگی به کتابخانه خارجی.
 * الگوریتم استاندارد تبدیل جلالی (دقیق برای سال‌های ۱۳۰۰ تا ۱۴۵۰ شمسی).
 *
 * الزام سند: تمام تاریخ‌های برنامه باید شمسی و با اعداد فارسی نمایش داده شوند
 * (بخش‌های ۷، ۶۱، ۳۳۰، ۸۴۶، ۲۰۴۶ سند اصلی).
 */
object PersianDateFormatter {

    private val weekDayNames = arrayOf(
        "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"
    )

    private val monthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    data class ShamsiDate(val year: Int, val month: Int, val day: Int, val dayOfWeek: Int)

    /** تبدیل یک timestamp میلادی (epoch millis) به تاریخ شمسی */
    fun toShamsi(epochMillis: Long, timeZone: TimeZone = TimeZone.getTimeZone("Asia/Tehran")): ShamsiDate {
        val cal = Calendar.getInstance(timeZone)
        cal.timeInMillis = epochMillis
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekCalendar = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday ... 7=Saturday
        val (jy, jm, jd) = gregorianToJalali(gYear, gMonth, gDay)
        // Calendar.DAY_OF_WEEK: 1=Sunday..7=Saturday -> این دقیقا با ایندکس weekDayNames یکی است
        return ShamsiDate(jy, jm, jd, dayOfWeekCalendar - 1)
    }

    /** فرمت کامل: «سه‌شنبه ۶ مرداد ۱۴۰۵» با اعداد فارسی */
    fun formatFull(epochMillis: Long): String {
        val d = toShamsi(epochMillis)
        val dayFa = PersianNumberFormatter.toPersianDigits(d.day.toString())
        val yearFa = PersianNumberFormatter.toPersianDigits(d.year.toString())
        return "${weekDayNames[d.dayOfWeek]} $dayFa ${monthNames[d.month - 1]} $yearFa"
    }

    /** فرمت کوتاه عددی: «۱۴۰۵/۰۵/۰۶» */
    fun formatShort(epochMillis: Long): String {
        val d = toShamsi(epochMillis)
        val mm = d.month.toString().padStart(2, '0')
        val dd = d.day.toString().padStart(2, '0')
        return PersianNumberFormatter.toPersianDigits("${d.year}/$mm/$dd")
    }

    /** فقط سال شمسی جاری - برای شماره‌گذاری خودکار پیش‌فاکتور (مثال: ۱۴۰۵) */
    fun currentShamsiYear(epochMillis: Long = System.currentTimeMillis()): Int {
        return toShamsi(epochMillis).year
    }

    // --- الگوریتم تبدیل میلادی به جلالی (Jalaali Calendar Algorithm) ---
    private fun div(a: Int, b: Int): Int = a / b
    private fun mod(a: Int, b: Int): Int = a - div(a, b) * b

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + (div(gy2 + 3, 4)) - (div(gy2 + 99, 100)) +
            (div(gy2 + 399, 400)) + gd + gDaysInMonth[gm - 1]
        var jy = -1595 + (33 * div(days, 12053))
        days = mod(days, 12053)
        jy += 4 * div(days, 1461)
        days = mod(days, 1461)
        if (days > 365) {
            jy += div(days - 1, 365)
            days = mod(days - 1, 365)
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + div(days, 31)
            jd = 1 + mod(days, 31)
        } else {
            jm = 7 + div(days - 186, 30)
            jd = 1 + mod(days - 186, 30)
        }
        return Triple(jy, jm, jd)
    }
}
