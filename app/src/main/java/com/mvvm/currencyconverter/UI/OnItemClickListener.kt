package com.mvvm.currencyconverter.UI

import com.mvvm.currencyconverter.data.RateItemObject

interface OnItemClickListener {
    fun onBaseItemUpdated(item: RateItem)
    fun onBaseItemUpdated(item: RateItemObject)

    // TODO add a function when an item is clicked (rename the onBaseItemUpdated)
    // TODO add a function to be called when an edit is made
}
