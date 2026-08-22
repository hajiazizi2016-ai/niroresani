package com.rasoulhajiazizi.niroresani.core.common

object PersianNumberFormatter {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

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

    fun toLatinDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val idx = persianDigits.indexOf(ch)
            when {
                idx >= 0 -> sb.append(idx)
                ch in '\u0660'..'\u0669' -> sb.append(ch - '\u0660')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

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

    fun formatRial(amount: Long): String = "${formatThousands(amount)} ریال"
}
