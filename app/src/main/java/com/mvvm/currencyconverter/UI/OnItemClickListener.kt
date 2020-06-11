package com.mvvm.currencyconverter.UI

import com.mvvm.currencyconverter.data.CurrencyItem

//interface to handle actions coming from the CurrencyListAdapter
interface OnItemClickListener {
    fun onItemClicked(item: CurrencyItem)
    fun onValueUpdated(value : Double)
}
