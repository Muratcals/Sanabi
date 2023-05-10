package com.example.sanabi.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class NewOrderCreateViewModel : ViewModel() {
    private lateinit var db: RoomServices
    val repository = Repository()
    val progress = MutableLiveData<Boolean>()
    val orderItems = MutableLiveData<List<BasketProductModelData>>()
    val saysItems = MutableLiveData<List<CategoryProducts>>()

    fun getBasket(view:View): Flow<List<BasketProductModelData>> {
        db=DatabaseRoom.getDatabase(view.context)
        return db.roomDb().getAllBaskets().flowOn(Dispatchers.IO)
    }
    fun getOrder(view: View) {
        progress.postValue(true)
        orderItems.postValue(arrayListOf())
        viewModelScope.launch(Dispatchers.IO) {
            db = DatabaseRoom.getDatabase(view.context)
            val items = db.roomDb().getAllBasket()
            orderItems.postValue(items)
            progress.postValue(false)
        }
    }
    fun decimalFormet(double: Double): String {
        val otherSymbol = DecimalFormatSymbols()
        otherSymbol.decimalSeparator = ','
        otherSymbol.groupingSeparator = '.'
        val df = DecimalFormat("#.##")
        df.decimalFormatSymbols = otherSymbol
        df.roundingMode = RoundingMode.DOWN
        return df.format(double)
    }

    fun updateOrder(items: BasketProductModelData) {
        viewModelScope.launch(Dispatchers.IO) {
            db.roomDb().updateBasket(items)
        }
    }

    fun getSaysProduct(view:View) {
        progress.value = true
        saysItems.value= arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {
            db=DatabaseRoom.getDatabase(view.context)
            val list = ArrayList<CategoryProducts>()
            val items = db.roomDb().getBasketCategoryId()
            for (item in items) {
                val repo = repository.getCategoryProducts(item)
                repo.enqueue(object : Callback<GetCategoryProductsModel> {
                    override fun onResponse(
                        call: Call<GetCategoryProductsModel>,
                        response: Response<GetCategoryProductsModel>
                    ) {
                        val result = response.body()!!.data.products
                        list.addAll(result)
                        saysItems.postValue(list)
                    }
                    override fun onFailure(call: Call<GetCategoryProductsModel>, t: Throwable) {
                        println(t.localizedMessage)
                    }
                })
            }
            progress.postValue(false)
        }
    }

    fun addOrder(items: BasketProductModelData) {
        viewModelScope.launch(Dispatchers.IO) {
            db.roomDb().insertBasket(items)
        }
    }
    fun deleteOrder(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.roomDb().deleteAllBaskets(id)
        }
    }
}