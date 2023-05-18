package com.example.sanabi.viewModel

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.SplashActivity
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

    val repository = Repository()
    val user = MutableLiveData<Data>()
    val progress = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    fun getUserInformation() {
        progress.value = true
        val repo = repository.getIdUser(util.auth.currentUser!!.email.toString())
        repo.enqueue(object : Callback<GetIdModel> {
            override fun onResponse(call: Call<GetIdModel>, response: Response<GetIdModel>) {
                val repo2 = repository.getIdUserData(response.body()!!.data)
                repo2.enqueue(object : Callback<GetUserInformation> {
                    override fun onResponse(
                        call: Call<GetUserInformation>,
                        response: Response<GetUserInformation>
                    ) {
                        user.value = response.body()!!.data
                        progress.value = false
                        error.value = false
                    }

                    override fun onFailure(call: Call<GetUserInformation>, t: Throwable) {
                        error.value = true
                        progress.value = false
                    }

                })
            }

            override fun onFailure(call: Call<GetIdModel>, t: Throwable) {

            }

        })
        viewModelScope.launch {
            val result = repository.getAllData()
            if (result.isSuccessful) {
                for (datas in result.body()!!.data) {
                    if (datas.mail.equals(util.auth.currentUser)) {
                        user.value = datas
                        break
                    }
                }
                progress.value = false
                error.value = false
            } else {
                println(result.errorBody())
                progress.value = false
                error.value = true
            }
        }
    }

    fun deleteAccount(activity: Activity) {
        progress.value = true
        if (util.auth.currentUser != null) {
            util.auth.currentUser!!.delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    val googleSingIn = GoogleSignIn.getClient(activity, gso)
                    googleSingIn.signOut()
                    util.auth.signOut()
                    deleteCustomer(activity)
                }
            }.addOnFailureListener {
                if (it.localizedMessage!!.equals("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")) {
                    val alertBuilder = AlertDialog.Builder(activity)
                    val alert = alertBuilder.create()
                    alertBuilder.setTitle("Doğrulanmamış hesap uyarısı")
                    alertBuilder.setMessage("Hesabınızı doğrulamadan bir silme işlemi yapamazsınız.Mail adresinize doğrulama işlemi göndermemizi ister misiniz ?\n Not: Eğer hesabızını doğruladıysanız çıkış yapıp tekrar girmeyi deneyiniz.")
                    alertBuilder.setPositiveButton(
                        "Gönder",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            util.auth.currentUser!!.sendEmailVerification().addOnSuccessListener {
                                Toast.makeText(
                                    activity.applicationContext,
                                    "Mail adresine doğrulama işlemi başarıyla gönderilmiştir.Hesabınızı doğruladıktan sonra çıkış yapıp tekrar girdikten sonra silme işlemini yapabilirsinz..",
                                    Toast.LENGTH_SHORT
                                ).show()
                                alert.cancel()
                            }.addOnFailureListener {
                                println(it.localizedMessage)
                            }
                        })
                    alertBuilder.setNegativeButton(
                        "Gönderme",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            alert.cancel()
                        })
                    alertBuilder.show()
                }
                progress.value = false
            }
        } else {
            deleteCustomer(activity)
        }
    }

    fun deleteCustomer(activity: Activity) {
        val deleteAccount = repository.deleteCustomer(util.customerId)
        deleteAccount.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                val sharedPreferences =
                    activity.getSharedPreferences("AddressId", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    this.putInt("addressId", 0)
                }
                val sharedPreferencesPayment =
                    activity.getSharedPreferences("PaymentMethod", Context.MODE_PRIVATE)
                val editorPayment = sharedPreferencesPayment.edit()
                editorPayment.apply {
                    this.putInt("paymentMethod", 0)
                }
                util.customerId = 0
                viewModelScope.launch(Dispatchers.IO) {
                    val database = DatabaseRoom.getDatabase(activity.applicationContext)
                    val searchDatabase=DatabaseRoom.getSearchDatabase(activity.applicationContext)
                    searchDatabase.searchRoomDb().deletePastSearch()
                    database.roomDb().deleteAllBasket()
                    progress.postValue(false)
                }
                Toast.makeText(activity, "Kullanıcı başarıyla silinmiştir", Toast.LENGTH_SHORT)
                    .show()
                activity.runOnUiThread {
                    val intent =Intent(activity.applicationContext,SplashActivity::class.java)
                    intent.putExtra("incoming","delete")
                    startActivity(activity.applicationContext,intent,null)
                    activity.finish()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }
        })
    }
}