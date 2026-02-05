package com.guarantify.util.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

object MoneyParser {

    fun parseToMinorUnits(input: String, currencyCode: String): Long? {
        if (input.isBlank() || input == ".") return null

        val bd = input.trim().toBigDecimalOrNull() ?: return null
        if (bd < BigDecimal.ZERO) return null

        val currency = runCatching {
            Currency.getInstance(currencyCode)
        }.getOrNull()

        val digits = currency?.defaultFractionDigits?.takeIf { it >= 0 } ?: 2

        return try {
            bd.movePointRight(digits)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

}