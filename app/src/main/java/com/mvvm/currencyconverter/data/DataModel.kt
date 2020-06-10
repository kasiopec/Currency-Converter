package com.mvvm.currencyconverter.data

import com.mvvm.currencyconverter.controller.Contract
import java.lang.Exception
import java.util.*
import kotlin.math.abs

class DataModel(private val presenter: Contract.Presenter) {
    private val items = mutableListOf<RateItem>()

    var baseItem: RateItem? = null
        set(value) { // see https://kotlinlang.org/docs/reference/properties.html#backing-fields
            if (field == value || value == null) {
                // Nothing to update, and we don't permit setting null
                return
            }

//            if(originalPosition == -1){
//                originalPosition = 0
//            }
            // The old base item is no longer the base item
            value.isBaseItem = true
            field?.isBaseItem = false
            field = value
            var originalPosition = items.indexOf(value)
            Collections.swap(items, originalPosition, 0)
            presenter.notifyListItemMoved(originalPosition, 0)
            presenter.notifyListItemUpdated(0)
            presenter.notifyListItemUpdated(originalPosition)

        }

    var oldRates: Map<String, Double> = hashMapOf()


    fun getItemsData(): MutableList<RateItem> {
        return items
    }

    fun updateAmountValue(value : Double) {
        baseItem?.amount = value
        for(item in items){
            if(item == baseItem){
                continue
            }
            computeNewAmounts(item.rate, item)
        }
        presenter.notifyListItemRangeUpdated(1, items.size-1)
    }

    private fun getRate(currency: String): Double =
        when {
            baseItem == null -> 0.0
            currency == baseItem?.currency -> 1.0
            else -> oldRates[currency] ?: 0.0
        }

    // Updates recyclerview rates values
    fun refreshData(rates: Map<String, Double>) {
        val theBaseItem = baseItem ?: return
        for (position in 0 until items.size) {
            val item = items[position]
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue
            }

            val oldRate = oldRates[item.currency]
            val newRate = rates[item.currency] ?: continue

            if (oldRate != null && abs(oldRate - newRate) < 0.0005) {
                continue
            }

            // TODO update the model amount and rate
            //TODO createa a new
            computeNewAmounts(newRate, item)
            presenter.notifyListItemUpdated(position)
        }
        oldRates = rates
    }

    fun computeNewAmounts(rate: Double, item: RateItem) {
        item.rate = rate
        item.amount = rate * (baseItem?.amount ?: 0.0)

    }


    //initialization/mapping base currency to rates
    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
        if (items.size > 0) {
            throw Exception("Cant initialize more than once")
        }
        val newBaseItem = RateItem(currency = baseCurrency, isBaseItem = true)
        newBaseItem.amount = 10.0
        println(newBaseItem.amount)
        newBaseItem.rate = 1.0
        items.add(newBaseItem)
        baseItem = newBaseItem
        for (rate in rates.keys) {
            if (rate == baseCurrency) {
                // Base currency was already added explicitly
                continue
            }
            val theBaseItem = baseItem ?: return

            val item = RateItem(currency = rate)
            val oldRate = oldRates[rate]
            val newRate = rates[rate] ?: continue
            //println(oldRate.toString()+ " : " + newRate)

            if (oldRate != null && abs(oldRate - newRate) < 0.0005) {
                item.rate = oldRate
                item.amount = oldRate * theBaseItem.amount
            } else {
                item.rate = newRate
                item.amount = newRate * theBaseItem.amount
            }
            // TODO set rate and amount here directly (don't call refreshData below)
            items.add(item)
            presenter.notifyListItemUpdated(items.indexOf(item))
        }
        //base currency to work with
        oldRates = rates
        //refreshData(rates)
    }


/*
        fun initialize(rates: Map<String, Double>, baseCurrency: String) {
            items.clear()
            //base currency to work with
            val newBaseItem = RateItem(currency = baseCurrency, isBaseItem = true)
            newBaseItem.amount = 10.0
            newBaseItem.rate = 1.0
            items.add(newBaseItem)
            for (rate in rates.keys) {
                if (rate == baseCurrency) {
                    // Base currency was already added explicitly
                    continue
                }

                // TODO set rate and amount here directly (don't call refreshData below)

                items.add(RateItem(currency = rate))
            }



            //newestRates = rates
            refreshData(rates)
            //presenter.notifyListItemsUpdated()
            //updateRates(rates)
        }
*/


}