package com.example.sanabi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sanabi.API.Repository
import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.model.GetAddressModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class LastOrderContentViewModel : ViewModel() {

    val orderList=MutableLiveData<OrderProductModel>()
    val orderContent=MutableLiveData<OrderProductModel>()
    val addressInformation=MutableLiveData<GetAddressModel>()
    val progress=MutableLiveData<Boolean>()
    val repository=Repository()


    fun getAllLastOrder(){
        val order=repository.getAllLastOrder()
        progress.value=true
        order.enqueue(object:Callback<OrderProductModel>{
            override fun onResponse(
                call: Call<OrderProductModel>,
                response: Response<OrderProductModel>
            ) {
                if (response.isSuccessful){
                    if (response.body()?.isNotEmpty()==true){
                        orderList.value=response.body()
                    }
                    progress.value=false
                }
            }

            override fun onFailure(call: Call<OrderProductModel>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }

        })
    }

    fun getAddress(id:Int){
        progress.value=true
        val address=repository.getAddressInformation(id)
        address.enqueue(object:Callback<GetAddressModel>{
            override fun onResponse(
                call: Call<GetAddressModel>,
                response: Response<GetAddressModel>
            ) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        addressInformation.value=response.body()
                        progress.value=false
                    }
                }
            }

            override fun onFailure(call: Call<GetAddressModel>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }
        })
    }

    fun getLastOrder(id:Int){
        progress.value=true
        val order=repository.getLastOrderContent(id)
        order.enqueue(object:Callback<OrderProductModel>{
            override fun onResponse(
                call: Call<OrderProductModel>,
                response: Response<OrderProductModel>
            ) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        println(response.body()!!.size)
                        orderContent.value=response.body()!!
                        getAddress(response.body()!![0].adressId)
                    }
                }
            }

            override fun onFailure(call: Call<OrderProductModel>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }

        })
    }
}