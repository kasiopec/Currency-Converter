package com.mvvm.currencyconverter.data

import com.google.gson.annotations.SerializedName

//Currency object with the variables defined in the JSON endpoint
data class CurrencyData(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
