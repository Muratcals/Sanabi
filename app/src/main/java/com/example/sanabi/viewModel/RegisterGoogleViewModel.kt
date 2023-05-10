package com.example.sanabi.viewModel

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
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

    val repository = Repository()
    fun postData(eMail: String, activity: AppCompatActivity, userInformation: Data) {
        val result = repository.postUserData(userInformation)
        result.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                saveDatabase(activity, eMail)
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(
                    activity.applicationContext.applicationContext,
                    t.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
                println(t.localizedMessage.toString())

            }

        })

    }

    fun save(activity: AppCompatActivity) {
        val googleSignIn = GoogleSignIn.getClient(activity.applicationContext, util.gso)
        val credential =
            GoogleAuthProvider.getCredential(googleSignIn.silentSignIn().result.idToken, null)
        util.auth.signInWithCredential(credential).addOnSuccessListener {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun saveDatabase(activity: AppCompatActivity, eMail: String) {
        val hashMap = hashMapOf<String, Any>()
        hashMap.put("Email", eMail)
        hashMap.put("login", "Google")
        util.database.collection("User Information").add(hashMap).addOnSuccessListener {
            Toast.makeText(activity.applicationContext, "Kullanıcı eklendi", Toast.LENGTH_SHORT)
                .show()
            save(activity)
        }
    }
}