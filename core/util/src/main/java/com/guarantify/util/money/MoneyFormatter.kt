package com.guarantify.util.money

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {

    fun minorUnitsToString(
        amountMinor: Long,
        currencyCode: String,
        locale: Locale = Locale.getDefault()
    ): String {
        val currency = runCatching { Currency.getInstance(currencyCode) }.getOrNull()
        val fractionDigits = currency?.defaultFractionDigits?.takeIf { it >= 0 } ?: 2

        val amountMajor = BigDecimal(amountMinor).movePointLeft(fractionDigits)

        val nf = NumberFormat.getCurrencyInstance(locale)
        if (currency != null) nf.currency = currency

        return nf.format(amountMajor)
    }

}