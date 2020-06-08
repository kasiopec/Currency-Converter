package com.mvvm.currencyconverter.controller

import android.content.Context
import android.os.Handler
import android.os.Looper

import com.mvvm.currencyconverter.data.CurrencyData
import com.mvvm.currencyconverter.data.CurrencyEndpointAPI
import com.mvvm.currencyconverter.data.RateItemObject
import com.mvvm.currencyconverter.data.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.abs

class Presenter(val view : Contract.View):Contract.Presenter{
    private val items = mutableListOf<RateItemObject>()
    var baseItem = RateItemObject("")
    var newestRates: Map<String, Double> = hashMapOf()

    val stopCall = false //handler stopper in case it's needed
    val mHandler = Handler(Looper.getMainLooper())

    //Retrofit initialization
    private val request: CurrencyEndpointAPI = ServiceBuilder.buildService(CurrencyEndpointAPI::class.java)
    private lateinit var call: Call<CurrencyData>

    override fun getItemsData() : MutableList<RateItemObject> {
        return items
    }

    override fun updateJsonCall(currency: String) {
        call = request.getCurrencyRates(currency)
    }

    fun getRates() : Map<String, Double>{
        return newestRates
    }

    override fun receiveBaseItem() : RateItemObject{
        return baseItem
    }

    fun startFetching(){
        call = request.getCurrencyRates("EUR")
        mHandler.post(object : Runnable {
            override fun run() {
                fetchData()
                mHandler.postDelayed(this, 1000)
            }
        })
    }
    //Method to fetch the data from the endpoint
    private fun fetchData() {
        call.clone().enqueue(object : Callback<CurrencyData> {
            override fun onResponse(
                call: Call<CurrencyData>,
                response: Response<CurrencyData>
            ) {
                //catching non failing codes
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }
                println("Code: $response")
                println(response.body())

                val result = requireNotNull(response.body())
                if (items.size == 0) {
                    initialize(result.rates, result.base)
                } else {
                    // TODO Verify that the base in adapter is equal to the response base
                    updateRates(result.rates)
                }
                //updating timer value
                view.updateTimerText()
            }

            //method to catch failed calls
            override fun onFailure(call: Call<CurrencyData>, t: Throwable) {
                println(t.message)
            }
        })
    }

    //initialization of the rates
    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
        items.clear()
        //base currency to work with
        baseItem = RateItemObject(currency = baseCurrency)
        items.add(baseItem)
        for (rate in rates.keys) {
            if (rate == baseCurrency) {
                // Base currency was already added explicitly
                continue
            }
            items.add(RateItemObject(currency = rate))
        }
        updateRates(rates)
    }

    fun updateRates(rates: Map<String, Double>) {
        for (position in 0 until items.size) {
            val item = items[position]
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue;
            }

            val oldRate = newestRates[item.currency]
            val newRate = rates[item.currency]

            if (oldRate != null && newRate != null && abs(oldRate - newRate) < 0.0005) {
                continue;
            }
        }
        newestRates = rates
        view.updateRecyclerViewData(newestRates)
    }

    override fun getUpdateTime(): Date {
        return Calendar.getInstance().time
    }

}