package com.mvvm.currencyconverter.controller

import com.mvvm.currencyconverter.UI.TestAdapter
import com.mvvm.currencyconverter.data.RateItem
import java.util.*

interface Contract {
    // Contains the stuff in the view that will be accessed by the presenter
    interface View {
        fun updateTimerText(date: Date)
        fun notifyListItemsUpdated()
        fun notifyListItemMoved(startPos : Int, endPos : Int)
        fun notifyListItemUpdated(itemPos : Int)
    }

    interface Presenter {
        fun itemClicked(item: RateItem)
        fun getItems() : List<RateItem>
        fun notifyListItemsUpdated()
        fun notifyListItemMoved(startPos : Int, endPos : Int)
        fun notifyListItemUpdated(itemPos : Int)
        fun updateValue(item : RateItem, value : Double)
    }
}