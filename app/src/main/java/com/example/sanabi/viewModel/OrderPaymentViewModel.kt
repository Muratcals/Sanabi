package com.example.sanabi.viewModel

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.model.CreateOrderModel
import com.example.sanabi.model.GetAddressModel
import com.example.sanabi.model.PaymentTypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderPaymentViewModel : ViewModel() {
    val paymentProducts = MutableLiveData<List<BasketProductModelData>>()
    val paymentType =MutableLiveData<PaymentTypeModel>()
    val addressInformation=MutableLiveData<GetAddressModel>()
    val repository=Repository()
    val progressBar = MutableLiveData<Boolean>()
    private lateinit var db: RoomServices

    fun getProduct(view: View): Flow<List<BasketProductModelData>> {
        progressBar.value = true
        db = DatabaseRoom.getDatabase(view.context)
        return db.roomDb().getAllBaskets()
    }

    fun getPaymentType(){
        val payment =repository.getAllPaymentType()
        payment.enqueue(object:Callback<PaymentTypeModel>{
            override fun onResponse(
                call: Call<PaymentTypeModel>,
                response: Response<PaymentTypeModel>
            ) {
                if (response.body()!=null){
                    paymentType.value=response.body()
                }

            }

            override fun onFailure(call: Call<PaymentTypeModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })
    }
    fun getAddress(id:Int){
        val result =repository.getAddressInformation(id)
        result.enqueue(object:Callback<GetAddressModel>{
            override fun onResponse(
                call: Call<GetAddressModel>,
                response: Response<GetAddressModel>
            ) {
                if (response.body()!=null){
                    addressInformation.value=response.body()
                }
            }

            override fun onFailure(call: Call<GetAddressModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })
    }

    fun orderSuccess(activity: Activity, order: CreateOrderModel){
        val postOrder =repository.postNewOrder(order)
        postOrder.enqueue(object :Callback<CreateOrderModel>{
            override fun onResponse(
                call: Call<CreateOrderModel>,
                response: Response<CreateOrderModel>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(activity.applicationContext, "Siparişiniz başarıyla oluşturuldu", Toast.LENGTH_SHORT).show()
                    viewModelScope.launch(Dispatchers.IO) {
                        db.roomDb().deleteAllBasket()
                        activity.finish()
                    }
                }
            }

            override fun onFailure(call: Call<CreateOrderModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })

    }
}