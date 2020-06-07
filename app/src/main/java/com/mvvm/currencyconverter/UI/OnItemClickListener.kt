package com.mvvm.currencyconverter.UI

import com.mvvm.currencyconverter.data.Rate

interface OnItemClickListener {
    fun onItemClicked(rate: Rate, position: Int)
}
