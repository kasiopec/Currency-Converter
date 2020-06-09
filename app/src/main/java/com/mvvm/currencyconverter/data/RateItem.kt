package com.mvvm.currencyconverter.data

data class RateItem(
    val currency: String,
    var isBaseItem : Boolean = false
) {
    var amount : Double = 0.0
    var rate: Double = 1.0
}

