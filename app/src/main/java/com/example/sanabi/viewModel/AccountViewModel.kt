package com.example.sanabi.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.Util.util
import com.example.sanabi.model.Data
import com.example.sanabi.model.GetIdModel
import com.example.sanabi.model.GetUserInformation
import com.example.sanabi.model.UserInformation
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
}