package com.rasoulhajiazizi.niroresani.core.common

object InputValidators {

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }

    fun validateQuantity(rawInput: String): ValidationResult {
        val normalized = PersianNumberFormatter.toLatinDigits(rawInput).trim()
        val value = normalized.toDoubleOrNull()
            ?: return ValidationResult.Invalid("تعداد وارد شده معتبر نیست")
        if (value <= 0.0) return ValidationResult.Invalid("تعداد باید بزرگ‌تر از صفر باشد")
        return ValidationResult.Valid
    }

    fun validatePrice(rawInput: String): ValidationResult {
        val normalized = PersianNumberFormatter.toLatinDigits(rawInput).trim()
        val value = normalized.toLongOrNull()
            ?: return ValidationResult.Invalid("قیمت وارد شده معتبر نیست")
        if (value < 0L) return ValidationResult.Invalid("قیمت نمی‌تواند منفی باشد")
        return ValidationResult.Valid
    }

    fun validateRequiredText(rawInput: String, fieldLabel: String): ValidationResult {
        if (rawInput.trim().isEmpty()) return ValidationResult.Invalid("$fieldLabel نمی‌تواند خالی باشد")
        return ValidationResult.Valid
    }
}
