package com.mvvm.currencyconverter.data

import com.mvvm.currencyconverter.data.CurrencyData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyEndpointAPI {

    //method to access JSON data from the endpoint
    @GET("latest")
    fun getCurrencyRates(@Query("base" ) cur : String): Call<CurrencyData>
}