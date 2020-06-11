package com.mvvm.currencyconverter.UI

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.data.CurrencyItem

class CurrencyListAdapter(
    var context: Context,
    private val items: List<CurrencyItem>,
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
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorSkyBlue))
        } else {
            holder.currencyValue.visibility = View.VISIBLE
            holder.etCurrencyValue.visibility = View.GONE
            holder.currencyRate.visibility = View.VISIBLE
            holder.card.setCardBackgroundColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener {
            listener.onItemClicked(item)
        }

        holder.etCurrencyValue.onSubmit {
            //non empty field check
            if (holder.etCurrencyValue.text.toString().trim().isNotEmpty() ||
                holder.etCurrencyValue.text.toString().trim().isNotBlank()) {
                val newValue = holder.etCurrencyValue.text.toString()
                listener.onValueUpdated(newValue.toDouble())
                holder.etCurrencyValue.hideKeyboard()
            } else {
                return@onSubmit
            }
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
    //hides keyboard
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
    val card : CardView = itemView.findViewById(R.id.cardView)
}
