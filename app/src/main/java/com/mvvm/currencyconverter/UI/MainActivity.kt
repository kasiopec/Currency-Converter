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

        recyclerAdapter = TestAdapter(
            this,
            presenter.getItemsData(),
            presenter.getRates(),
            presenter.receiveBaseItem(),
            this
        )
        recyclerAdapter.setHasStableIds(false)
        recyclerView.adapter = recyclerAdapter
    }

    //data formatter for the text view
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onBaseItemUpdated(item: RateItem) {
        //TODO Remove it from the interface later, just some leftovers
        //requesting new rates for base currency
        //call = request.getCurrencyRates(recyclerAdapter.baseItem.currency)
        //scrolling to recyclerview top
        //recyclerView.layoutManager!!.scrollToPosition(0)
    }

    override fun onBaseItemUpdated(item: RateItemObject) {
        //requesting new rates for base currency
        presenter.updateJsonCall(item.currency)
        //scrolling to recyclerview top
        recyclerView.layoutManager!!.scrollToPosition(0)
    }

    override fun updateTimerText(date : Date) {
        val updateTime = date.toString("HH:mm:ss")
        updateText.text = getString(R.string.update_text, updateTime)
    }

    override fun updateRecyclerViewData(newestRates: Map<String, Double>) {
        //TODO add refresh data method inside adapter and notifydatasetchanged?
        recyclerAdapter.refreshData(newestRates)

    }
}


