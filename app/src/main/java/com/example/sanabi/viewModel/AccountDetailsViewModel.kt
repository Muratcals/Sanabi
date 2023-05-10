package com.example.sanabi.viewModel


import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sanabi.API.Repository
import com.example.sanabi.Util.util
import com.example.sanabi.model.Data
import com.example.sanabi.model.GetUserInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountDetailsViewModel : ViewModel() {

    var user =MutableLiveData<Data>()
    val error=MutableLiveData<Boolean>()
    val progress=MutableLiveData<Boolean>()
    val repository=Repository()
    val numberControl=MutableLiveData<Boolean>()
    val upperLetterControl=MutableLiveData<Boolean>()
    val lowerLetterControl=MutableLiveData<Boolean>()
    val passwordSizeControl=MutableLiveData<Boolean>()


    fun passwordNumberControl(password:String){
        val number ="1234567890"
        val control =number.filter {
            if(password.contains(it)){
                return@filter true
            }
            return@filter false
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
    fun getUserData(){
        repository.getIdUserData(util.customerId).enqueue(object :Callback<GetUserInformation>{
            override fun onResponse(
                call: Call<GetUserInformation>,
                response: Response<GetUserInformation>
            ) {
                println(response.body()!!.data.mail)
                user.value=response.body()!!.data
            }

            override fun onFailure(call: Call<GetUserInformation>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
    }

    fun updateUser(activity:AppCompatActivity,users :Data){
        val repo =repository.updateUser(users)
        repo.enqueue(object :Callback<Data>{
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Toast.makeText(activity, "Güncellendi", Toast.LENGTH_SHORT).show()
                activity.onBackPressed()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                println(t.localizedMessage)
                Toast.makeText(activity, "Bir sorun oluştu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updatePassword(activity: AppCompatActivity,password:String){
        util.auth.currentUser!!.updatePassword(password).addOnSuccessListener {
            Toast.makeText(activity, "Şifreniz başarıyla güncellenmiştir", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }
    }

}