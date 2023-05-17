package com.example.sanabi.viewModel

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.MainActivity
import com.example.sanabi.Util.util
import com.example.sanabi.model.Data
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterGoogleViewModel : ViewModel() {

    val progress =MutableLiveData<Boolean>()
    val repository = Repository()
    fun postData(activity: AppCompatActivity, userInformation: Data) {
        progress.value=true
        val result = repository.postUserData(userInformation)
        result.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                save(activity,response.body()!!.id)
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(
                    activity.applicationContext.applicationContext,
                    t.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
                progress.value=false
            }
        })
    }

    fun delete(id:Int){
        val result = repository.deleteCustomer(id)
        result.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {

            }
            override fun onFailure(call: Call<Data>, t: Throwable) {

            }
        })
    }

    fun save(activity: AppCompatActivity,id:Int) {
        val googleSignIn = GoogleSignIn.getClient(activity.applicationContext, util.gso)
        val credential = GoogleAuthProvider.getCredential(googleSignIn.silentSignIn().result.idToken, null)
        util.auth.signInWithCredential(credential).addOnSuccessListener {
            Toast.makeText(activity.applicationContext, "Kaydınız oluşturulmuştur.Google ile giriş yapabilirsiniz", Toast.LENGTH_SHORT).show()
            progress.value=false
            activity.finish()
        }.addOnFailureListener {
            delete(id)
            progress.value=false
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}