package com.mvvm.currencyconverter

import com.google.gson.Gson
import com.mvvm.currencyconverter.data.CurrencyData
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

class DataMappingTest {
    fun `base field is mapped correctly`() {
        val json = "{\"rates\":{\"CAD\":1.5237,\"HKD\":8.7809,\"ISK\":148.9,\"PHP\":56.457,\"DKK\":7.4564,\"HUF\":344.62,\"CZK\":26.589,\"AUD\":1.6227,\"RON\":4.8382,\"SEK\":10.425,\"IDR\":15882.4,\"INR\":85.63,\"BRL\":5.7329,\"RUB\":77.8155,\"HRK\":7.5715,\"JPY\":123.77,\"THB\":35.65,\"CHF\":1.0866,\"SGD\":1.5775,\"PLN\":4.4425,\"BGN\":1.9558,\"TRY\":7.6747,\"CNY\":8.0349,\"NOK\":10.5403,\"NZD\":1.7392,\"ZAR\":19.0823,\"USD\":1.133,\"MXN\":24.6466,\"ILS\":3.9172,\"GBP\":0.89448,\"KRW\":1365.57,\"MYR\":4.8345},\"base\":\"EUR\",\"date\":\"2020-06-05\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)

        assertEquals("EUR", parsed.base)
    }
}

class RateManagerUnitTest {
    fun updatingRates_changesAmounts() {
        // TODO create a rate manager
        // TODO add some items to the list
        // TODO update the rates
        // TODO assert that amount have changed
    }
}
