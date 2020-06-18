package com.mvvm.currencyconverter.controller

import android.os.Handler
import android.os.Looper
import com.mvvm.currencyconverter.data.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Presenter(val view : Contract.View):Contract.Presenter{

    val dataModel = DataModel(this)
    var isInitialized = false
    val stopCall = false //handler stopper in case it's needed
    val mHandler = Handler(Looper.getMainLooper())

    // Retrofit initialization
    private val request: CurrencyEndpointAPI = ServiceBuilder
        .buildService(CurrencyEndpointAPI::class.java)
    private lateinit var call: Call<CurrencyData>

    //changes base currency and updates Json call
    override fun itemClicked(item: CurrencyItem) {
        dataModel.baseItem = item
        call = request.getCurrencyRates(item.currency)
        fetchData()
    }

    //Gets the list of the items from the data model
    override fun getItems() : List<CurrencyItem>{
        return dataModel.getItemsData()
    }
    //Updates value of the base item
    override fun updateAmountValue(value: Double) {
        dataModel.updateAmountValue(value)
    }

    /*
    * Block of adapter update functions
    * */
    override fun notifyListItemsUpdated() {
        view.notifyListItemsUpdated()
    }

    override fun notifyListItemMoved(startPos: Int, endPos: Int) {
        view.notifyListItemMoved(startPos, endPos)
    }

    override fun notifyListItemUpdated(itemPos: Int) {
        view.notifyListItemUpdated(itemPos)
    }

    override fun notifyListItemRangeUpdated(startPost: Int, size: Int) {
        view.notifyListItemRangeUpdated(startPost, size)
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
     fun fetchData() {
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
                val result = requireNotNull(response.body())
                //check to initialize items once otherwise just refresh data
                if(!isInitialized){
                    dataModel.initialize(result.rates, result.base)
                    isInitialized = true
                }else{
                    dataModel.refreshData(result.rates)
                }
                val updateTime = Calendar.getInstance().time
                view.updateTimerText(updateTime)
            }
            //method to catch failed calls
            override fun onFailure(call: Call<CurrencyData>, t: Throwable) {
                println(t.message)
            }
        })
    }




}