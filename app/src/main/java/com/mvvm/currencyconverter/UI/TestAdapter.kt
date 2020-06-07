package com.mvvm.currencyconverter.UI


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.data.Rate
import java.util.*


class TestAdapter(var currencies: MutableList<String>, var curRates: MutableList<Double>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<TestViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item, parent, false)
        return TestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        return holder.bind(currencies[position], curRates[position], listener)
    }


    fun updateCurrencies(newNames: MutableList<String>, newRates: MutableList<Double>){
        currencies = newNames
        curRates = newRates
        notifyDataSetChanged()
    }

    fun swapItems(posStart: Int, posEnd: Int) {
        Collections.swap(currencies, posStart, posEnd)
        Collections.swap(curRates, posStart, posEnd)
        notifyItemMoved(posStart, posEnd)
    }


}


class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var amount = 1
    val currencyName: TextView = itemView.findViewById(R.id.currencyName)
    val currencyRate: TextView = itemView.findViewById(R.id.currencyRate)
    val currencyValue: TextView = itemView.findViewById(R.id.currencyValue)
    val et_currencyValue: EditText = itemView.findViewById(R.id.et_currencyValue)


    fun bind(currency: String, rateVal: Double, listener: OnItemClickListener) {
//        currencyName.text = rate.name
//        currencyRate.text = "1:"+rate.rateValue.toString()
//        currencyValue.text = (amount * rate.rateValue).toString()
        itemView.setOnClickListener {
           // listener.onItemClicked(rate, adapterPosition)

            et_currencyValue.visibility = View.VISIBLE
            et_currencyValue.setText(amount.toString())
            currencyValue.visibility = View.GONE
        }
        et_currencyValue.setOnClickListener {
            et_currencyValue.onSubmit { submit() }
        }


//        itemView.setOnClickListener { v: View ->
//            val position: Int = adapterPosition
//            swapItems(position, 0)
//        }

    }

    fun EditText.onSubmit(func: () -> Unit){
        setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                func()
            }
            true
        }
    }

    fun submit(){
        amount = et_currencyValue.text.toString().toInt()
        et_currencyValue.visibility = View.GONE
        currencyValue.visibility = View.VISIBLE
    }
}







