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


class CurrenciesAdapter(
    var rates: List<Rate>,
    private var rateValues: List<Double>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CurrenciesViewHolder>() {
    var amount = 1.0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrenciesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item, parent, false)
        return CurrenciesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    override fun onBindViewHolder(holder: CurrenciesViewHolder, position: Int) {
        holder.currencyName.text = rates[position].name
        holder.currencyRate.text = "1:" + rateValues[position]
        holder.currencyValue.text = "%.2f".format(amount * rateValues[position])
        holder.itemView.setOnClickListener {
            listener.onItemClicked(rates[position], position)
            if (position == 0) {
                holder.etCurrencyValue.visibility = View.VISIBLE
                holder.etCurrencyValue.setText(amount.toString())
                holder.currencyValue.visibility = View.GONE
            } else {
                holder.etCurrencyValue.visibility = View.GONE
                holder.currencyValue.visibility = View.VISIBLE
            }
        }
        holder.etCurrencyValue.onSubmit {
            amount = holder.etCurrencyValue.text.toString().toDouble()
            holder.etCurrencyValue.hideKeyboard()
            holder.etCurrencyValue.visibility = View.GONE
            holder.currencyValue.visibility = View.VISIBLE
            notifyDataSetChanged()
        }


        //return holder.bind(rates[position], rateValues[position], listener)
    }

    fun updateCurrencies(newRates: MutableList<Rate>) {
        rates = newRates
        notifyDataSetChanged()
        println("called from adapter")
    }

    fun updateCurrencyRates(newRates: MutableList<Rate>) {
        val updatedRates = mutableListOf<Double>()
        for (rate in newRates) {
            updatedRates.add(rate.rateValue)
        }
        rateValues = updatedRates
        notifyDataSetChanged()
    }

    fun swapItems(posStart: Int, posEnd: Int) {
        Collections.swap(rates, posStart, posEnd)
        Collections.swap(rateValues, posStart, posEnd)
        notifyItemMoved(posStart, posEnd)
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
    val card : CardView = itemView.findViewById(R.id.cardView)
}









