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
    lateinit var recyclerAdapter: TestAdapter
    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = Presenter(this)
        presenter.startFetching()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerAdapter = TestAdapter(this, presenter.getItems(), this)
        //recyclerAdapter.setHasStableIds(true)
        recyclerView.adapter = recyclerAdapter
    }

    //data formatter for the text view
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onItemClicked(item: RateItem) {
        //requesting new rates for base currency
        presenter.itemClicked(item)
        //scrolling to recyclerview top
        recyclerView.layoutManager!!.scrollToPosition(0)
    }

    override fun onValueUpdated(item: RateItem, newValue: Double) {
        presenter.updateAmountValue(item, newValue)
    }

    override fun updateTimerText(date : Date) {
        val updateTime = date.toString("HH:mm:ss")
        updateText.text = getString(R.string.update_text, updateTime)
    }

    override fun notifyListItemsUpdated() {
        //val item = presenter.getItems()[1]
       // println("CALLED ON FETCH"+item.rate + item.currency + item.amount)
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


