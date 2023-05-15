package com.example.sanabi.viewModel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.util
import com.example.sanabi.Util.util.gso
import com.example.sanabi.model.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountViewModel : ViewModel() {

    val repository =Repository()
    val user=MutableLiveData<Data>()
    val progress =MutableLiveData<Boolean>()
    val error =MutableLiveData<Boolean>()

    fun getUserInformation(){
        progress.value=true
        val repo =repository.getIdUser(util.auth.currentUser!!.email.toString())
        repo.enqueue(object: Callback<GetIdModel>{
            override fun onResponse(call: Call<GetIdModel>, response: Response<GetIdModel>) {
                val repo2 =repository.getIdUserData(response.body()!!.data)
                repo2.enqueue(object :Callback<GetUserInformation>{
                    override fun onResponse(call: Call<GetUserInformation>, response: Response<GetUserInformation>) {
                        user.value=response.body()!!.data
                        progress.value=false
                        error.value=false
                    }

                    override fun onFailure(call: Call<GetUserInformation>, t: Throwable) {
                        error.value=true
                        progress.value=false
                    }

                })
            }

            override fun onFailure(call: Call<GetIdModel>, t: Throwable) {

            }

        })
        viewModelScope.launch {
            val result =repository.getAllData()
            if (result.isSuccessful){
                for (datas in result.body()!!.data){
                    if (datas.mail.equals(util.auth.currentUser)){
                        user.value=datas
                        break
                    }
                }
                progress.value=false
                error.value=false
            }else{
                println(result.errorBody())
                progress.value=false
                error.value=true
            }
        }
    }

    fun deleteAccount(activity:Activity){
        progress.value=true
        val getCustomerAddress =repository.getSelectedAddress(util.customerId)
        getCustomerAddress.enqueue(object:Callback<AddressModel>{
            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {
                if (response.body()!=null){
                    for (items in response.body()!!.data){
                        val deleteAddress =repository.deleteAddress(items.id)
                        deleteAddress.enqueue(object :Callback<AddressData>{
                            override fun onResponse(
                                call: Call<AddressData>,
                                response: Response<AddressData>
                            ) {

                            }
                            override fun onFailure(call: Call<AddressData>, t: Throwable) {
                                println(t.localizedMessage)
                            }
                        })
                    }
                }
            }
            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
    }
    fun deleteCustomer(activity: Activity){
        progress.value=true
        val deleteAccount =repository.deleteCustomer(util.customerId)
        deleteAccount.enqueue(object :Callback<Data>{
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                activity.deleteSharedPreferences("AddressId")
                activity.deleteSharedPreferences("PaymentMethod")
                viewModelScope.launch(Dispatchers.IO) {
                    val database =DatabaseRoom.getDatabase(activity.applicationContext)
                    database.roomDb().deleteAllBasket()
                    println("şuana kadar başarılı")
                }
                util.customerId=0
                util.auth.currentUser!!.delete().addOnSuccessListener {
                    val googleSingIn=GoogleSignIn.getClient(activity,gso)
                    googleSingIn.signOut()
                    util.auth.signOut()
                    Toast.makeText(activity, "Kullanıcı başarıyla silinmiştir", Toast.LENGTH_SHORT).show()
                    progress.value=false
                }
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
    }
}