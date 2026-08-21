package com.example.rentmanagement.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, symbol: String = Constants.DEFAULT_CURRENCY_SYMBOL): String {
        val nf = NumberFormat.getNumberInstance(Locale("en", "IN"))
        nf.maximumFractionDigits = 2
        nf.minimumFractionDigits = 0
        return "$symbol${nf.format(amount)}"
    }
}
