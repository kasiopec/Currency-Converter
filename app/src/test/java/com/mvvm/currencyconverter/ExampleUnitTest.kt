package com.mvvm.currencyconverter


import com.google.gson.Gson
import com.mvvm.currencyconverter.UI.MainActivity
import com.mvvm.currencyconverter.controller.Contract
import com.mvvm.currencyconverter.controller.Presenter
import com.mvvm.currencyconverter.data.CurrencyData
import com.mvvm.currencyconverter.data.DataModel
import com.mvvm.currencyconverter.data.RateItem
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.Exception
import java.util.*
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

    fun `base field is mapped correctly`() {
        val json = "{\"rates\":{\"CAD\":1.5237,\"HKD\":8.7809,\"ISK\":148.9,\"PHP\":56.457,\"DKK\":7.4564,\"HUF\":344.62,\"CZK\":26.589,\"AUD\":1.6227,\"RON\":4.8382,\"SEK\":10.425,\"IDR\":15882.4,\"INR\":85.63,\"BRL\":5.7329,\"RUB\":77.8155,\"HRK\":7.5715,\"JPY\":123.77,\"THB\":35.65,\"CHF\":1.0866,\"SGD\":1.5775,\"PLN\":4.4425,\"BGN\":1.9558,\"TRY\":7.6747,\"CNY\":8.0349,\"NOK\":10.5403,\"NZD\":1.7392,\"ZAR\":19.0823,\"USD\":1.133,\"MXN\":24.6466,\"ILS\":3.9172,\"GBP\":0.89448,\"KRW\":1365.57,\"MYR\":4.8345},\"base\":\"EUR\",\"date\":\"2020-06-05\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)

        assertEquals("EUR", parsed.base)
    }
}

class ModelTest {
    var model = createModel()
    val presenter = FakePresenter()
    fun createModel() :DataModel {
        val json = "{\"rates\":{\"CAD\":1.5237,\"HKD\":8.7809,\"ISK\":148.9,\"PHP\":56.457,\"DKK\":7.4564,\"HUF\":344.62,\"CZK\":26.589,\"AUD\":1.6227,\"RON\":4.8382,\"SEK\":10.425,\"IDR\":15882.4,\"INR\":85.63,\"BRL\":5.7329,\"RUB\":77.8155,\"HRK\":7.5715,\"JPY\":123.77,\"THB\":35.65,\"CHF\":1.0866,\"SGD\":1.5775,\"PLN\":4.4425,\"BGN\":1.9558,\"TRY\":7.6747,\"CNY\":8.0349,\"NOK\":10.5403,\"NZD\":1.7392,\"ZAR\":19.0823,\"USD\":1.133,\"MXN\":24.6466,\"ILS\":3.9172,\"GBP\":0.89448,\"KRW\":1365.57,\"MYR\":4.8345},\"base\":\"EUR\",\"date\":\"2020-06-05\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)
        val model = DataModel(FakePresenter())
        model.initialize(parsed.rates, parsed.base)
        return model
    }

    @Test
    fun shouldUpdateBaseItemAmount() {
        model = createModel()
        val newEnteredValue = 20.0
        model.updateAmountValue(newEnteredValue)
        assertEquals(newEnteredValue, model.baseItem?.amount)
        // test that baseItem property is updated after setting it to a new value
        // test that baseItem property ignores null assignment
    }
    @Test
    fun shouldInitBaseItemPropertyAsEuro(){
        model = createModel()
        assertEquals("EUR", model.baseItem?.currency)
        // test that baseItem property is updated after setting it to a new value
        // test that baseItem property ignores null assignment
    }

    @Test
    fun shouldSetBaseItem(){
        model = createModel()
        val json = "{\"rates\":{\"CAD\":1.0,\"HKD\":5.7891384292,\"ISK\":98.9624376149,\"PHP\":37.2780404518,\"DKK\":4.895784082,\"HUF\":225.3283425269,\"CZK\":17.4737325978,\"GBP\":0.5842067245,\"RON\":3.1750065669,\"SEK\":6.8692540058,\"IDR\":10521.4276333071,\"INR\":56.4112161807,\"BRL\":3.6257551878,\"RUB\":51.3178355661,\"HRK\":4.9704491726,\"JPY\":80.2206461781,\"THB\":23.2610979774,\"CHF\":0.706724455,\"EUR\":0.6566850538,\"MYR\":3.1757945889,\"BGN\":1.2843446283,\"TRY\":5.0659968479,\"CNY\":5.2735093249,\"NOK\":6.920344103,\"NZD\":1.139808248,\"ZAR\":12.3703703704,\"USD\":0.7469792488,\"MXN\":16.3025348043,\"SGD\":1.0340162858,\"AUD\":1.0651431573,\"ILS\":2.5640267928,\"KRW\":887.9498292619,\"PLN\":2.9238245338},\"base\":\"CAD\",\"date\":\"2020-06-10\"}"
        val parsed = Gson().fromJson(json, CurrencyData::class.java)
        model.initialize(parsed.rates, parsed.base)
        assertEquals(true, model.baseItem?.isBaseItem)
        assertEquals(parsed.base, model.baseItem?.currency)
    }
    @Test
    fun shouldReturnListOfItems(){
        model= createModel()
        val itemList = model.getItemsData()
        assertNotNull(itemList)
    }
    @Test(expected = Exception::class)
    fun shouldThrowAnErrorIfInitializedTwice(){
        model = createModel()
        val map = HashMap<String, Double>()
        val currency = "EUR"
        model.initialize(map, currency)
    }

    @Test
    fun shouldBecalledXTimes(){
        model = createModel()
        val bla = presenter.updatedtimes
        assertEquals(20, bla)
    }


}

class FakePresenter: Contract.Presenter {
    val list = listOf<RateItem>()
    val model  = DataModel(this)
    var updatedtimes = 0
    override fun itemClicked(item: RateItem) {
        model.baseItem = item
    }

    override fun getItems(): List<RateItem> {
        return list
    }

    override fun notifyListItemsUpdated() {
        updatedtimes++
    }

    override fun notifyListItemMoved(startPos: Int, endPos: Int) {
    }

    override fun notifyListItemUpdated(itemPos: Int) {

    }

    override fun notifyListItemRangeUpdated(startPost: Int, size: Int) {
    }

    override fun updateAmountValue(value: Double) {

    }

}


