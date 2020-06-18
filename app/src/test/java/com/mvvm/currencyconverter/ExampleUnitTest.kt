package com.mvvm.currencyconverter


import com.google.gson.Gson
import com.mvvm.currencyconverter.UI.MainActivity
import com.mvvm.currencyconverter.controller.Contract
import com.mvvm.currencyconverter.controller.Presenter
import com.mvvm.currencyconverter.data.CurrencyData
import com.mvvm.currencyconverter.data.DataModel
import com.mvvm.currencyconverter.data.CurrencyItem
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import java.lang.Exception
import kotlin.collections.HashMap

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
    @Test
    fun `base field is mapped correctly`() {
        val json = "{\"rates\":{\"CAD\":1.5237,\"HKD\":8.7809,\"ISK\":148.9,\"PHP\":56.457,\"DKK\":7.4564,\"HUF\":344.62,\"CZK\":26.589,\"AUD\":1.6227,\"RON\":4.8382,\"SEK\":10.425,\"IDR\":15882.4,\"INR\":85.63,\"BRL\":5.7329,\"RUB\":77.8155,\"HRK\":7.5715,\"JPY\":123.77,\"THB\":35.65,\"CHF\":1.0866,\"SGD\":1.5775,\"PLN\":4.4425,\"BGN\":1.9558,\"TRY\":7.6747,\"CNY\":8.0349,\"NOK\":10.5403,\"NZD\":1.7392,\"ZAR\":19.0823,\"USD\":1.133,\"MXN\":24.6466,\"ILS\":3.9172,\"GBP\":0.89448,\"KRW\":1365.57,\"MYR\":4.8345},\"base\":\"EUR\",\"date\":\"2020-06-05\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)

        assertEquals("EUR", parsed.base)
    }
}

class ModelTest {
    private val presenter = FakePresenter()
    var model = DataModel(presenter)
    private fun createModel() :DataModel {
        val json = "{\"rates\":{\"CAD\":1.5237,\"HKD\":8.7809,\"ISK\":148.9,\"PHP\":56.457,\"DKK\":7.4564,\"HUF\":344.62,\"CZK\":26.589,\"AUD\":1.6227,\"RON\":4.8382,\"SEK\":10.425,\"IDR\":15882.4,\"INR\":85.63,\"BRL\":5.7329,\"RUB\":77.8155,\"HRK\":7.5715,\"JPY\":123.77,\"THB\":35.65,\"CHF\":1.0866,\"SGD\":1.5775,\"PLN\":4.4425,\"BGN\":1.9558,\"TRY\":7.6747,\"CNY\":8.0349,\"NOK\":10.5403,\"NZD\":1.7392,\"ZAR\":19.0823,\"USD\":1.133,\"MXN\":24.6466,\"ILS\":3.9172,\"GBP\":0.89448,\"KRW\":1365.57,\"MYR\":4.8345},\"base\":\"EUR\",\"date\":\"2020-06-05\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)
        model.initialize(parsed.rates, parsed.base)
        return model
    }

    @Test
    fun shouldUpdateBaseItemAmount() {
        createModel()
        val newEnteredValue = 20.0
        model.updateAmountValue(newEnteredValue)
        assertEquals(newEnteredValue, model.baseItem?.amount)
    }
    @Test
    fun shouldInitBaseItemPropertyAsEuro(){
        createModel()
        assertEquals("EUR", model.baseItem?.currency)
    }

    @Test
    fun shouldUpdateOldRatesList(){
        createModel()
        val newRates : Map<String, Double> = hashMapOf("CAD" to 10.0, "DKK" to 5.0, "EUR" to 20.0)
        model.refreshData(newRates)
        assertEquals(newRates, model.oldRates)
    }
    @Test
    fun shouldReturnListOfItems(){
        createModel()
        val itemList = model.getItemsData()
        assertNotNull(itemList)
    }
    @Test(expected = Exception::class)
    fun shouldThrowAnErrorIfInitializedTwice(){
        createModel()
        val map = HashMap<String, Double>()
        val currency = "EUR"
        model.initialize(map, currency)
    }

    @Test
    fun shouldUpdateOnlyDkkRateValue(){
        val rates : Map<String, Double> = hashMapOf("CAD" to 10.0, "DKK" to 5.0, "RUB" to 20.0)
        val newRates : Map<String, Double> = hashMapOf("CAD" to 10.0, "DKK" to 2.0, "RUB" to 20.0)
        model.initialize(rates, "EUR")
        model.refreshData(newRates)
        val eur = model.getItemsData()[0]
        val dkk = model.getItemsData()[1]
        val rub = model.getItemsData()[2]
        val cad = model.getItemsData()[3]
        assertEquals(2.0, dkk.rate, 3.0)
        assertEquals(20.0, rub.rate, 0.0)
        assertEquals(10.0, cad.rate, 0.0)
        assertEquals(1.0, eur.rate, 0.0)
    }

    @Test
    fun shouldOnlyCallOneAdapterUpdateByRefreshData(){
        // 5 times from initialization + 1 from refreshData cuz of new rate
        val presenter = FakePresenter()
        val model = DataModel(presenter)
        val rates : Map<String, Double> = hashMapOf("CAD" to 10.0, "DKK" to 2.0, "RUB" to 20.0)
        val newRates : Map<String, Double> = hashMapOf("CAD" to 10.0, "DKK" to 5.0, "RUB" to 20.0)
        model.initialize(rates, "EUR")
        var update = presenter.updatedTimes
        assertEquals(5, update)
        model.refreshData(newRates)
        update = presenter.updatedTimes
        assertEquals(6, update)
    }
}

class FakePresenter: Contract.Presenter {
    private val list = listOf<CurrencyItem>()
    var updatedTimes = 0
    override fun itemClicked(item: CurrencyItem) {
    }

    override fun getItems(): List<CurrencyItem> {
        return list
    }

    override fun notifyListItemsUpdated() {
    }

    override fun notifyListItemMoved(startPos: Int, endPos: Int) {
    }

    override fun notifyListItemUpdated(itemPos: Int) {
        updatedTimes++
    }

    override fun notifyListItemRangeUpdated(startPost: Int, size: Int) {
    }

    override fun updateAmountValue(value: Double) {

    }

}


