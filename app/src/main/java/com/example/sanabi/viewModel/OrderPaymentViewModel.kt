package com.example.sanabi.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.model.GetAddressModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class OrderPaymentViewModel : ViewModel() {
    val paymentProducts = MutableLiveData<List<BasketProductModelData>>()
    val addressInformation=MutableLiveData<GetAddressModel>()
    val repository=Repository()
    val progressBar = MutableLiveData<Boolean>()
    private lateinit var db: RoomServices

    fun getProduct(view: View): Flow<List<BasketProductModelData>> {
        progressBar.value = true
        db = DatabaseRoom.getDatabase(view.context)
        return db.roomDb().getAllBaskets()
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
}