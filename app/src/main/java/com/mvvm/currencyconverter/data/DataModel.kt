package com.mvvm.currencyconverter.data

import com.mvvm.currencyconverter.controller.Contract
import java.lang.Exception
import java.util.*
import kotlin.math.abs

class DataModel(private val presenter: Contract.Presenter) {
    private val items = mutableListOf<CurrencyItem>()
    var oldRates: Map<String, Double> = hashMapOf()
    var baseItem: CurrencyItem? = null
        set(value) { // see https://kotlinlang.org/docs/reference/properties.html#backing-fields
            if (field == value || value == null) {
                // Nothing to update, and we don't permit setting null
                return
            }
            // The old base item is no longer the base item
            value.isBaseItem = true
            field?.isBaseItem = false
            field = value
            val originalPosition = items.indexOf(value)
            Collections.swap(items, originalPosition, 0)
            presenter.notifyListItemMoved(originalPosition, 0)
            presenter.notifyListItemUpdated(0)
            presenter.notifyListItemUpdated(originalPosition)
        }
    //Returns items list for later use (adapter)
    fun getItemsData(): MutableList<CurrencyItem> {
        return items
    }

    //updates base item amount & calculates amount for all other items
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

    // Updates recyclerview rates values
    fun refreshData(rates: Map<String, Double>) {
        for (position in 0 until items.size) {
            val item = items[position]
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue
            }
            val oldRate = oldRates[item.currency]
            val newRate = rates[item.currency] ?: continue

            //No need to update rates if they are the same
            if (oldRate != null && abs(oldRate - newRate) < 0.0005) {
                continue
            }
            computeNewAmounts(newRate, item)
            presenter.notifyListItemUpdated(position)
        }
        oldRates = rates
    }

    //Calculates amounts for the currencies
    private fun computeNewAmounts(rate: Double, item: CurrencyItem) {
        item.rate = rate
        item.amount = rate * (baseItem?.amount ?: 0.0)

    }

    //initialization/mapping currency to rates
    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
        if (items.size > 0) {
            throw Exception("Cant initialize more than once")
        }
        val newBaseItem = CurrencyItem(currency = baseCurrency, isBaseItem = true)
        newBaseItem.amount = 10.0
        newBaseItem.rate = 1.0
        items.add(newBaseItem)
        baseItem = newBaseItem
        for (rate in rates.keys) {
            if (rate == baseCurrency) {
                // Base currency was already added
                continue
            }
            //check for base item if not drop
            val theBaseItem = baseItem ?: return
            val newRate = rates[rate] ?: continue
            val item = CurrencyItem(currency = rate)
            item.rate = newRate
            item.amount = newRate * theBaseItem.amount
            items.add(item)
            presenter.notifyListItemUpdated(items.indexOf(item))
        }
        oldRates = rates
    }
}