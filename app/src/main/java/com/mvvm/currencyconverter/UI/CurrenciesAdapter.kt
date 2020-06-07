package com.mvvm.currencyconverter.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.data.Rate
import java.util.*

data class RateItem(
    val currency: String,
    var amount: Double
)

class CurrenciesAdapter(
    var rates: List<Rate>,
    private var rateValues: List<Double>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CurrenciesViewHolder>() {
    // Used for looking up the rates; updated frequently
    lateinit var newestRates: Map<String, Double>

    lateinit var baseItem: RateItem

    private val items = mutableListOf<RateItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrenciesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_currency_item, parent, false)
        return CurrenciesViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return items[position].currency.hashCode().toLong()
    }

    fun initialize(rates: Map<String, Double>, baseCurrency: String) {
        items.clear()

        baseItem = RateItem(currency = baseCurrency, amount = 10.0)
        items.add(baseItem)

        for (rate in rates.keys) {
            if (rate == baseCurrency) {
                // Base currency was already added explicitly
                continue
            }

            items.add(RateItem(currency = rate, amount = 0.0))
        }

        updateRates(rates)
    }

    fun updateRates(rates: Map<String, Double>) {
        newestRates = rates
        updateAmounts()

        // Update everything except the base item
        notifyItemRangeChanged(1, items.size - 1)
    }

    private fun updateAmounts() {
        for (item in items) {
            if (item == baseItem) {
                // The base item amount isn't updated as it was entered by the user
                continue;
            }

            val rate = newestRates[item.currency]

            item.amount = if (rate != null) rate  * baseItem.amount else 0.0
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getRate(currency: String): Double =
        if (currency == baseItem.currency) {
             1.0
        } else {
            newestRates[currency] ?: 0.0
        }

    private fun updateBaseItem(item: RateItem) {
        if (baseItem == item) {
            // Nothing to update
            return
        }

        baseItem = item
        val originalPosition = items.indexOf(item)
        Collections.swap(items, originalPosition, 0)
        notifyItemMoved(originalPosition, 0)
    }

    override fun onBindViewHolder(holder: CurrenciesViewHolder, position: Int) {
        val item = items[position]
        val rate = getRate(item.currency)

        holder.currencyName.text = item.currency
        holder.currencyRate.text = "%.2f".format(Locale.getDefault(), rate)
        holder.currencyValue.text = "%.2f".format(item.amount)
        holder.etCurrencyValue.setText("%.2f".format(item.amount))

        if (item == baseItem) {
            holder.currencyValue.visibility = View.GONE
            holder.etCurrencyValue.visibility = View.VISIBLE
        } else {
            holder.currencyValue.visibility = View.VISIBLE
            holder.etCurrencyValue.visibility = View.GONE

        }

        holder.itemView.setOnClickListener {
            updateBaseItem(item)
            listener.onBaseItemUpdated()
            /*listener.onItemClicked(rates[position], position)
            if (position == 0) {
                holder.etCurrencyValue.visibility = View.VISIBLE
                holder.etCurrencyValue.setText(amount.toString())
                holder.currencyValue.visibility = View.GONE
            } else {
                holder.etCurrencyValue.visibility = View.GONE
                holder.currencyValue.visibility = View.VISIBLE
            }

             */
        }

        holder.etCurrencyValue.onSubmit {
            if (item != baseItem) {
                return@onSubmit
            }

            // TODO guard against non-numeric input
            baseItem.amount = holder.etCurrencyValue.text.toString().toDouble()
            notifyItemRangeChanged(1, items.size)
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

class CurrenciesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val currencyName: TextView = itemView.findViewById(R.id.currencyName)
    val currencyRate: TextView = itemView.findViewById(R.id.currencyRate)
    val currencyValue: TextView = itemView.findViewById(R.id.currencyValue)
    val etCurrencyValue: EditText = itemView.findViewById(R.id.et_currencyValue)
}
