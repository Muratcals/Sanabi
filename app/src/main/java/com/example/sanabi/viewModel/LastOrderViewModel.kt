package com.example.sanabi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sanabi.API.Repository
import com.example.sanabi.LastOrderModel.OrderProductModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LastOrderViewModel : ViewModel() {

    val customerOrderList=MutableLiveData<OrderProductModel>()
    val repository=Repository()

    fun getCustomerOrders(){
        val customerOrders=repository.getAllLastOrder()
        customerOrders.enqueue(object:Callback<OrderProductModel>{
            override fun onResponse(
                call: Call<OrderProductModel>,
                response: Response<OrderProductModel>
            ) {
                if (response.body()!=null){
                    customerOrderList.value=response.body()
                }
            }
            override fun onFailure(call: Call<OrderProductModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })
    }
}