package com.mvvm.currencyconverter.UI

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.currencyconverter.R
import com.mvvm.currencyconverter.data.CurrencyData
import com.mvvm.currencyconverter.data.CurrencyEndpointAPI
import com.mvvm.currencyconverter.data.Rate
import com.mvvm.currencyconverter.data.ServiceBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), OnItemClickListener {
    lateinit var request: CurrencyEndpointAPI
    var rates = mutableListOf<Rate>()
    var curNames = mutableListOf<String>()
    var curRates = mutableListOf<Double>()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: CurrenciesAdapter
    lateinit var call: Call<CurrencyData>
    lateinit var baseCurrency : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stopCall = false //handler stopper
        val mHandler = Handler(Looper.getMainLooper())
        //creating first call for EUR currency
        request = ServiceBuilder.buildService(CurrencyEndpointAPI::class.java)
        call = request.getCurrencyRates("EUR")
        //recycleView setup
        recyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = CurrenciesAdapter(rates, curRates, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter


        //Handler to execute endpoint data fetch every second
        //(notifications from the API about the new data would be better though)
        mHandler.post(object : Runnable {
            override fun run() {
                fetchData()
                if (!stopCall) {
                    mHandler.postDelayed(this, 1000)
                }
            }
        })
    }

    //Method to fetch the data from the endpoint
    private fun fetchData() {
        call.clone().enqueue(object : Callback<CurrencyData> {
            override fun onResponse(
                call: Call<CurrencyData>,
                response: Response<CurrencyData>
            ) {
                //catching non failing codes
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    return
                }
                println("Code: $response")
                println(response.body())
                //updated list of currency rates
                val updatedRates = mutableListOf<Rate>()
                //getting base currency
                baseCurrency = response.body()!!.base
                //EUR doesn't have rate value when base, had to add it manually
                if(baseCurrency == "EUR"){
                    updatedRates.add(Rate(baseCurrency, 1.0))
                }
                //creating list of Rate
                for ((key, value) in response.body()!!.rates) {
                    updatedRates.add(Rate(key, value))
                }
                //initial recyclerview data check
                if (rates.size == 0 && curRates.size == 0) {
                    recyclerAdapter.updateCurrencies(updatedRates)
                    recyclerAdapter.updateCurrencyRates(updatedRates)
                }
                //recyclerAdapter.updateCurrencyRates(updatedRates)
                //val areEqual = isEqual(rates, updatedRates)
                val areEqual = isEqual2(rates, updatedRates)
                println(areEqual)
                println(rates)
                println(updatedRates)
                //checking if we got new rate values
                if (!areEqual) {
                    rates = updatedRates
                    recyclerAdapter.updateCurrencyRates(rates)
                    recyclerAdapter.updateCurrencies(rates)
                    recyclerAdapter.swapItems(getIndex(baseCurrency),0)
                }
                val date = getCurrentDateTime()
                updateText.text = "Updated at: " + date.toString("HH:mm:ss")
            }
            //method to catch failed calls
            override fun onFailure(call: Call<CurrencyData>, t: Throwable) {
                println(t.message)
            }
        })
    }

    //data formatter for the text view
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    //gets current time
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    //getting index of the base currency
    fun getIndex(wanted: String?): Int {
        for (i in 0 until rates.size) {
            if (rates[i].name == wanted) {
                return i
            }
        }
        return 0
    }
    //checks if elements and order is the same
    fun <T> isEqual(first: List<T>, second: List<T>): Boolean {
        if (first.size != second.size) {
            return false
        }
        return first.zip(second).all { (x, y) -> x == y }
    }

    //check if 2 lists have the same items (resource heavy)
    fun <T> isEqual2(first: List<T>, second: List<T>): Boolean {
        if(first.containsAll(second) && second.containsAll(first)) {
            return true
        }
        return false
    }

    override fun onItemClicked(rate: Rate, position: Int) {
        //requesting new rates for base currency
        call = request.getCurrencyRates(rate.name)
        //swapping items with each other
        recyclerAdapter.swapItems(position, 0)
        //scrolling to recyclerview top
        recyclerView.layoutManager!!.scrollToPosition(0)
    }
}


