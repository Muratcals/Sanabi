package com.example.sanabi.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.example.sanabi.API.Repository
import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.LastOrderModel.Product
import com.example.sanabi.OrderActivity
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.Util.util
import com.example.sanabi.model.BasketProductModelData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LastOrderViewModel : ViewModel() {

    val customerOrderList = MutableLiveData<OrderProductModel>()
    val repository = Repository()

    fun getCustomerOrders(id:Int) {
        val customerOrders = repository.getCustomerOrders(id)
        customerOrders.enqueue(object : Callback<OrderProductModel> {
            override fun onResponse(
                call: Call<OrderProductModel>,
                response: Response<OrderProductModel>
            ) {
                if (response.body() != null) {
                    customerOrderList.value = response.body()
                }
            }

            override fun onFailure(call: Call<OrderProductModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
    }

    fun addOrderBasket(activity: Activity, list: List<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            val database = DatabaseRoom.getDatabase(activity.applicationContext).roomDb()
            database.deleteAllBasket()
            for (items in list) {
                val product = items.product
                val data = BasketProductModelData(
                    product.categoryId,
                    product.createdDate?:"",
                    product.description,
                    items.productQuantity,
                    product.id,
                    product.image,
                    product.name,
                    product.price,
                    product.stock
                )
                database.insertBasket(data)
            }
            val intent =Intent(activity.applicationContext,OrderActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activity.applicationContext,intent,null)
            activity.finish()
        }
    }
}