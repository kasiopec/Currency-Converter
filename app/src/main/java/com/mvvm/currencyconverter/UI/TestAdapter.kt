package com.mvvm.currencyconverter.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.data.RateItemObject
import java.util.*
import kotlin.math.abs

class TestAdapter(
    var context: Context,
    private val items: MutableList<RateItemObject>,
    // TODO this goes out
    private var newestRates: Map<String, Double>,
    // TODO this goes out
    private var baseItem: RateItemObject,

    // TODO maybe wrap this more nicely
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TestViewHolder>() {
    var baseAmount = 10.0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item, parent, false)
        return TestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //Updates recyclerview rates values
    fun refreshData(rates : Map<String, Double>){
        for (position in 0 until items.size) {
            val item = items[position]
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue
            }

            val oldRate = newestRates[item.currency]
            val newRate = rates[item.currency]

            if (oldRate != null && newRate != null && abs(oldRate - newRate) < 0.0005) {
                continue
            }
            notifyItemChanged(position)
        }
        newestRates = rates
    }

    private fun getRate(currency: String): Double =
        if (currency == baseItem.currency) {
            1.0
        } else {
            newestRates[currency] ?: 0.0
        }
    //handles items swaps
    private fun updateBaseItem(item: RateItemObject) {
        if (baseItem == item) {
            // Nothing to update
            return
        }
        baseItem = item
        val originalPosition = items.indexOf(item)
        Collections.swap(items, originalPosition, 0)
        notifyItemMoved(originalPosition, 0)
        notifyItemChanged(0)

    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val item = items[position]
        val rate = getRate(item.currency)
        // TODO Don't do any computation in the adapter, it should simply present items
        val amount = rate * baseAmount
        val amountFormatted = "%.2f".format(amount)

        holder.currencyName.text = item.currency
        holder.currencyRate.text = context.resources.getString(R.string.rate_text, rate.toString())
        holder.currencyValue.text = amountFormatted
        holder.etCurrencyValue.setText(amountFormatted)

        if (item == baseItem) {
            holder.currencyRate.visibility = View.VISIBLE
            holder.currencyValue.visibility = View.GONE
            holder.etCurrencyValue.visibility = View.VISIBLE
        } else {
            holder.currencyValue.visibility = View.VISIBLE
            holder.etCurrencyValue.visibility = View.GONE
            holder.currencyRate.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            updateBaseItem(item)
            listener.onBaseItemUpdated(item)
        }

        holder.etCurrencyValue.onSubmit {
            if (item != baseItem) {
                return@onSubmit
            }

            // TODO guard against non-numeric input
            baseAmount = holder.etCurrencyValue.text.toString().toDouble()
            notifyItemRangeChanged(0, items.size)
            holder.etCurrencyValue.hideKeyboard()
        }
    }

    private fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                func()
            }
            true
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val currencyName: TextView = itemView.findViewById(R.id.currencyName)
    val currencyRate: TextView = itemView.findViewById(R.id.currencyRate)
    val currencyValue: TextView = itemView.findViewById(R.id.currencyValue)
    val etCurrencyValue: EditText = itemView.findViewById(R.id.et_currencyValue)
}
