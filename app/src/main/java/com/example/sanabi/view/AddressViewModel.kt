package com.example.sanabi.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sanabi.API.Repository
import com.example.sanabi.Util.util
import com.example.sanabi.model.AddressData
import com.example.sanabi.model.AddressModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel : ViewModel() {
    val addressList= MutableLiveData<List<AddressData>>()
    val repository= Repository()
    val progress = MutableLiveData<Boolean>()

    fun getAdress(){
        progress.value=true
        val result =repository.getSelectedAddress(util.customerId)
        result.enqueue(object : Callback<AddressModel> {
            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {
                addressList.value=response.body()!!.data
                progress.value=false
            }
            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }
        })
    }
}