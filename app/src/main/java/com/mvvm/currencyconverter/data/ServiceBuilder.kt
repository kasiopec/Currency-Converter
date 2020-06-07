package com.mvvm.currencyconverter.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    //OKHttp client for the retrofit
    private val client  = OkHttpClient.Builder().build()

    //retrofit object, contains base url
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.exchangeratesapi.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    //function that connects builder and interface, returns Retrofit object
    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }



}