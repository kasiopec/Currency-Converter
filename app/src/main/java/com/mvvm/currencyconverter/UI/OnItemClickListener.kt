package com.mvvm.currencyconverter.UI

import com.mvvm.currencyconverter.data.Rate
import com.mvvm.currencyconverter.data.RateItemObject

interface OnItemClickListener {
    fun onBaseItemUpdated(item: RateItem)
    fun onBaseItemUpdated(item: RateItemObject)
}
