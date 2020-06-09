package com.mvvm.currencyconverter.controller

import com.mvvm.currencyconverter.data.RateItemObject
import java.util.*

interface Contract {
    interface View{
        fun updateTimerText(date : Date)
        fun updateRecyclerViewData(newestRates : Map<String, Double>)
    }
    interface Presenter{
        fun getItemsData() : MutableList<RateItemObject>
        fun receiveBaseItem() : RateItemObject
        fun updateJsonCall(currency : String)
    }
}