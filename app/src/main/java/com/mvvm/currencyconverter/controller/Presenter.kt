package com.mvvm.currencyconverter.controller

import android.os.Handler
import android.os.Looper
import com.mvvm.currencyconverter.UI.TestAdapter
import com.mvvm.currencyconverter.data.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.abs

class Presenter(val view : Contract.View):Contract.Presenter{
    val dataModel = DataModel(this)

    val stopCall = false //handler stopper in case it's needed
    val mHandler = Handler(Looper.getMainLooper())

    // Retrofit initialization
    private val request: CurrencyEndpointAPI = ServiceBuilder.buildService(CurrencyEndpointAPI::class.java)
    private lateinit var call: Call<CurrencyData>

    //changes base currency for Json queries
    override fun itemClicked(item: RateItem) {
        dataModel.baseItem = item
        call = request.getCurrencyRates(item.currency)
    }

    override fun getItems() : List<RateItem>{
        return dataModel.getItemsData()
    }

    override fun notifyListItemsUpdated() {
        view.notifyListItemsUpdated()
    }

    override fun notifyListItemMoved(startPos: Int, endPos: Int) {
        view.notifyListItemMoved(startPos, endPos)
    }

    override fun notifyListItemUpdated(itemPos: Int) {
        view.notifyListItemUpdated(itemPos)
    }

    override fun updateValue(item: RateItem, value: Double) {
       dataModel.updateItemValue(value)
    }


    //starts very first json requests
    //and handler that calls for updates every second
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
                dataModel.initialize(result.rates, result.base)

                val updateTime = Calendar.getInstance().time
                //update view
                view.updateTimerText(updateTime)
                view.notifyListItemsUpdated()
            }

            //method to catch failed calls
            override fun onFailure(call: Call<CurrencyData>, t: Throwable) {
                println(t.message)
            }
        })
    }




}