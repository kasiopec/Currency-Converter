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
import kotlin.collections.HashMap


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
        recyclerAdapter.setHasStableIds(true)
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

                val result = requireNotNull(response.body())
                if (recyclerAdapter.itemCount == 0) {
                    recyclerAdapter.initialize(result.rates, result.base)
                } else {
                    // TODO Verify that the base in adapter is equal to the response base
                    recyclerAdapter.updateRates(result.rates)
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

    override fun onBaseItemUpdated() {
        //requesting new rates for base currency
        call = request.getCurrencyRates(recyclerAdapter.baseItem.currency)
        //scrolling to recyclerview top
        recyclerView.layoutManager!!.scrollToPosition(0)
    }
}


