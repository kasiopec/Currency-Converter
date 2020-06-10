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
import com.mvvm.currencyconverter.data.RateItem

class TestAdapter(
    var context: Context,
    private val items: List<RateItem>,
    // TODO maybe wrap this more nicely
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item, parent, false)
        return TestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }



    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val item = items[position]
        val amountFormatted = "%.2f".format(item.amount)
        holder.currencyName.text = item.currency
        holder.currencyRate.text = context.resources.getString(R.string.rate_text, item.rate.toString())
        holder.currencyValue.text = amountFormatted
        holder.etCurrencyValue.setText(amountFormatted)

        if (item.isBaseItem) {
            holder.currencyRate.visibility = View.INVISIBLE
            holder.currencyValue.visibility = View.GONE
            holder.etCurrencyValue.visibility = View.VISIBLE
        } else {
            holder.currencyValue.visibility = View.VISIBLE
            holder.etCurrencyValue.visibility = View.GONE
            holder.currencyRate.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            listener.onItemClicked(item)
        }

        holder.etCurrencyValue.onSubmit {
            // TODO guard against non-numeric input
            val newValue = holder.etCurrencyValue.text.toString().toDouble()
            if(newValue.toString()==""){
                return@onSubmit
            }
            listener.onValueUpdated(newValue)
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
