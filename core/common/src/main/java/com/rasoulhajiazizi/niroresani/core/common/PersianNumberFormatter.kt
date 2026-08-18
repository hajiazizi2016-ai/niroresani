package com.rasoulhajiazizi.niroresani.core.common

/**
 * ابزار نمایش اعداد فارسی و مبالغ ریالی.
 * الزام سند: تمام اعداد (تعداد، قیمت، مبلغ، شماره پیش‌فاکتور، تاریخ) باید
 * با ارقام فارسی و جداکننده هزارگان نمایش داده شوند (بخش‌های ۶۱، ۶۲، ۱۱۷۳، ۱۸۹۵).
 */
object PersianNumberFormatter {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /** تبدیل رشته حاوی ارقام لاتین به ارقام فارسی */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch.isDigit()) {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /** تبدیل ارقام فارسی/عربی ورودی کاربر به عدد لاتین قابل پردازش (برای فرم‌ها) */
    fun toLatinDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val idx = persianDigits.indexOf(ch)
            when {
                idx >= 0 -> sb.append(idx)
                ch in '\u0660'..'\u0669' -> sb.append(ch - '\u0660') // ارقام عربی هم پشتیبانی شود
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /** جداکننده هزارگان + تبدیل به فارسی، مثال: ۱٬۲۵۰٬۰۰۰ */
    fun formatThousands(amount: Long): String {
        val isNegative = amount < 0
        val absValue = kotlin.math.abs(amount).toString()
        val grouped = StringBuilder()
        var count = 0
        for (i in absValue.length - 1 downTo 0) {
            grouped.append(absValue[i])
            count++
            if (count % 3 == 0 && i != 0) grouped.append('٬')
        }
        val result = grouped.reverse().toString()
        val finalResult = if (isNegative) "-$result" else result
        return toPersianDigits(finalResult)
    }

    /** فرمت کامل مبلغ با واحد ریال، مثال: ۱٬۲۵۰٬۰۰۰ ریال */
    fun formatRial(amount: Long): String = "${formatThousands(amount)} ریال"
}
