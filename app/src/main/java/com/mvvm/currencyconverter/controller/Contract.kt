package com.mvvm.currencyconverter.controller

import java.util.*

interface Contract {
    interface View{
        fun updateTimerText()
        fun updateRecyclerViewData()
    }
    interface Presenter{
        fun getUpdateTime() : Date
        fun getItemsData() : MutableList<RateItem>
    }
}