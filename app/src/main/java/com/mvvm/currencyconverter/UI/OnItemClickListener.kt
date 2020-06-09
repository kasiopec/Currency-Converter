package com.mvvm.currencyconverter.UI

import com.mvvm.currencyconverter.data.RateItem

interface OnItemClickListener {
    fun onItemClicked(item: RateItem)
    fun onValueUpdated(item : RateItem, newValue : Double)

    // TODO add a function when an item is clicked (rename the onBaseItemUpdated)
    // TODO add a function to be called when an edit is made
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
