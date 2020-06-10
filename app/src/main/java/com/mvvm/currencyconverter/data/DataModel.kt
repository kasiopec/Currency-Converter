package com.mvvm.currencyconverter.data

import com.mvvm.currencyconverter.controller.Contract
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
            val originalPosition = items.indexOf(value)
            // The old base item is no longer the base item
            value.isBaseItem = true
            field?.isBaseItem = false
            field = value

            Collections.swap(items, originalPosition, 0)
            presenter.notifyListItemMoved(originalPosition, 0)
            presenter.notifyListItemUpdated(0)
        }

    var newestRates: Map<String, Double> = hashMapOf()


    fun getItemsData(): MutableList<RateItem> {
        return items
    }

    fun updateAmountValue(newAmount : Double){
        baseItem?.amount = newAmount
    }

    private fun getRate(currency: String): Double =
        when {
            baseItem == null -> 0.0
            currency == baseItem?.currency -> 1.0
            else -> newestRates[currency] ?: 0.0
        }

    // Updates recyclerview rates values
    private fun refreshData(rates: Map<String, Double>) {
        val theBaseItem = baseItem ?: return
        for (position in 0 until items.size) {
            val item = items[position]
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue
            }

            val oldRate = newestRates[item.currency]
            val newRate = rates[item.currency] ?: continue

            if (oldRate != null && abs(oldRate - newRate) < 0.0005) {
                continue
            }

            // TODO update the model amount and rate
            //TODO tell presenter about relevant data updates
            item.rate = newRate
            item.amount = newRate * theBaseItem.amount
            presenter.notifyListItemUpdated(position)
        }
        newestRates = rates
    }


    //initialization/mapping base currency to rates
    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
        items.clear()
        val newBaseItem = RateItem(currency = baseCurrency, isBaseItem = true)
        if(baseItem == null){
            newBaseItem.amount = 10.0
            newBaseItem.rate = 1.0
            items.add(newBaseItem)
            baseItem = newBaseItem
        }else{
            items.add(newBaseItem)
        }
        val theBaseItem = baseItem ?: return
        for (rate in rates.keys) {
            if (rate == baseCurrency) {
                // Base currency was already added explicitly
                continue
            }

            val item = RateItem(currency = rate)
            val oldRate = newestRates[rate]
            val newRate = rates[rate] ?: continue
            println(oldRate.toString()+ " : " + newRate)

            if (oldRate != null && abs(oldRate - newRate) < 0.0005) {
                item.rate = oldRate
                item.amount = oldRate * theBaseItem.amount
            }else{
                item.rate = newRate
                item.amount = newRate * theBaseItem.amount
            }

            // TODO set rate and amount here directly (don't call refreshData below)
            items.add(item)
            presenter.notifyListItemRangeUpdated(1, items.size-1)
        }
        //base currency to work with
        newestRates = rates
        //refreshData(rates)
    }

    /*    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
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