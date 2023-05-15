package com.example.sanabi.viewModel

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.sanabi.API.Repository
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.model.Data
import com.example.sanabi.model.GetIdModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailControlViewModel : ViewModel() {

    val error = MutableLiveData<Boolean>()
    val progress = MutableLiveData<Boolean>()
    val repository = Repository()

    fun getData(activity: Activity, eMail: String) {
        progress.value = true
        val bundle = bundleOf("Email" to eMail)
        viewModelScope.launch(  ) {
            val result = repository.getIdUser(eMail)
            result.enqueue(object :Callback<GetIdModel>{
                override fun onResponse(call: Call<GetIdModel>, response: Response<GetIdModel>) {
                    if (response.body()!!.data!=0) {
                        util.auth.fetchSignInMethodsForEmail(eMail).addOnSuccessListener {
                            if (it.signInMethods!=null){
                                if (it.signInMethods!![0].toString().equals("Google")){
                                    Toast.makeText(activity.applicationContext, "Bu mail google ile girişte kullanılmış. Google ile giriş yapmayı deneyiniz.", Toast.LENGTH_SHORT).show()
                                    activity.finish()
                                }else{
                                    activity.findNavController(R.id.fragmentContainerView).navigate(
                                        R.id.action_emailControlFragment_to_successMailFragment,bundle
                                    )
                                }
                            }
                        }

                    } else {
                        activity.findNavController(R.id.fragmentContainerView).navigate(
                            R.id.action_emailControlFragment_to_registerFragment,
                            bundle
                        )
                    }
                    progress.value=false
                }

                override fun onFailure(call: Call<GetIdModel>, t: Throwable) {
                    error.value = true
                    Toast.makeText(activity.applicationContext,t.toString(), Toast.LENGTH_SHORT)
                        .show()
                    progress.value=false
                }
            })

        }
    }
}