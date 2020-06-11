package com.mvvm.currencyconverter.controller

import com.mvvm.currencyconverter.data.CurrencyItem
import java.util.*

interface Contract {
    // View methods
    interface View {
        fun updateTimerText(date: Date)
        fun notifyListItemsUpdated()
        fun notifyListItemMoved(startPos : Int, endPos : Int)
        fun notifyListItemUpdated(itemPos : Int)
        fun notifyListItemRangeUpdated(startPost : Int, size : Int)
    }
    // presenter
    interface Presenter {
        fun itemClicked(item: CurrencyItem)
        fun getItems() : List<CurrencyItem>
        fun notifyListItemsUpdated()
        fun notifyListItemMoved(startPos : Int, endPos : Int)
        fun notifyListItemUpdated(itemPos : Int)
        fun notifyListItemRangeUpdated(startPost : Int, size : Int)
        fun updateAmountValue(value : Double)
    }
}


/*
The design idea:

- Adapter (UI-related) "View"
    Gets a list of items
    Creates views for those items (based on their properties)
    Notifies the presenter when an item is clicked
    Notifies the presenter when a value is entered in the EditText for an item

- Presenter
    Receive events from the adapter (item clicked, value updated)
    Refresh data from the server and sends it to the model

- Model (important to be testable)
    Keeps a list of RateItem objects
    Updates amounts when rates are refreshed
    Keeps track of which item is the base item
 */