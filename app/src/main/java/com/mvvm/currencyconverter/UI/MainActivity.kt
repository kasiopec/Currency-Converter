package com.mvvm.currencyconverter.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.controller.Contract
import com.mvvm.currencyconverter.controller.Presenter
import com.mvvm.currencyconverter.data.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClickListener, Contract.View {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: CurrencyListAdapter
    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = Presenter(this)
        presenter.startFetching()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerAdapter = CurrencyListAdapter(this, presenter.getItems(), this)
        recyclerView.adapter = recyclerAdapter
    }

    //handles item click in the recyclerView
    override fun onItemClicked(item: CurrencyItem) {
        presenter.itemClicked(item)
        //scrolling to recyclerview top
        recyclerView.layoutManager!!.scrollToPosition(0)
    }

    //handles submit button on the keyboard
    override fun onValueUpdated(value : Double) {
        presenter.updateAmountValue(value)
    }

    //handles timer text updates
    override fun updateTimerText(date : Date) {
        val updateTime = date.toString("HH:mm:ss")
        updateText.text = getString(R.string.update_text, updateTime)
    }

    //data formatter for the text view
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    /*
    * Block of adapter update functions
    * */
    override fun notifyListItemsUpdated() {
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun notifyListItemMoved(startPos: Int, endPos: Int) {
        recyclerAdapter.notifyItemMoved(startPos, endPos)
    }

    override fun notifyListItemUpdated(itemPos: Int) {
        recyclerAdapter.notifyItemChanged(itemPos)
    }

    override fun notifyListItemRangeUpdated(startPost: Int, size: Int) {
        recyclerAdapter.notifyItemRangeChanged(startPost, size)
    }


}


