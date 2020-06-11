package com.mvvm.currencyconverter.data

//Items which are created for
data class CurrencyItem(
    val currency: String,
    var isBaseItem : Boolean = false
) {
    var amount : Double = 0.0
    var rate: Double = 1.0
}

