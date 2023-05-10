package com.example.sanabi.viewModel

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.MainActivity
import com.example.sanabi.Util.util
import com.example.sanabi.model.Data
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterViewModel : ViewModel() {

    val numberControl=MutableLiveData<Boolean>()
    val upperLetterControl=MutableLiveData<Boolean>()
    val lowerLetterControl=MutableLiveData<Boolean>()
    val passwordSizeControl=MutableLiveData<Boolean>()
    val saveControl=MutableLiveData<Boolean>()
    val repository=Repository()

    fun passwordNumberControl(password:String){
        val number ="1234567890"
        val control =number.filter {
            if(password.contains(it)){
                numberControl.value=true
            }
            return@filter true
        }
        numberControl.value=control.isNotEmpty()
    }
    fun upperLetterControl(password:String){
        val letter ="QWERTYUIOPĞÜİŞLKJHGFDSAZXCVBNMÖÇ"
        val control =letter.filter {
            if(password.contains(it)){
                return@filter true
            }
            return@filter false
        }
        upperLetterControl.value=control.isNotEmpty()
    }
    fun lowerLetterControl(password:String){
        val letter ="qwertyuıopğüişlkjhgfdsazxcvbnmöç"
        val control =letter.filter {
            if(password.contains(it)){
                return@filter true
            }
            return@filter false
        }
        lowerLetterControl.value = control.isNotEmpty()
    }

    fun passwordsize(password: String){
        passwordSizeControl.value=password.length>10
    }

    fun postData(activity: AppCompatActivity,userInformation: Data,password: String){
        viewModelScope.launch {
            val result =repository.postUserData(userInformation)
            result.enqueue(object:Callback<Data>{
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    util.auth.createUserWithEmailAndPassword(userInformation.mail,password).addOnSuccessListener {
                        saveDatabase(activity,userInformation.mail)
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                    println(t.localizedMessage)
                    saveControl.value=false
                }

            })
        }
    }
    /*
    fun postDatas(view:View,userInformation: Data) {
        viewModelScope.launch {
            val result = repository.postUserData(userInformation)
            if (result.isSuccessful){
                Toast.makeText(view.context, "Kullanıcı eklendi", Toast.LENGTH_SHORT).show()
                saveControl.value = true
            }else{
                Toast.makeText(view.context, result.errorBody().toString(), Toast.LENGTH_SHORT).show()
                println(result.errorBody().toString())
                saveControl.value = false
            }
        }
    }

     */
    fun saveDatabase(activity: AppCompatActivity,eMail:String){
        val hashMap = hashMapOf<String,Any>()
        hashMap.put("Email",eMail)
        hashMap.put("login","Google")
        util.database.collection("User Information").add(hashMap).addOnSuccessListener {
            Toast.makeText(activity.applicationContext, "Kullanıcı eklendi", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}