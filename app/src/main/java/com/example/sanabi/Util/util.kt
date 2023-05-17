package com.example.sanabi.Util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import com.example.sanabi.Room.RoomServices
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.http.Headers

object  util {


    //https://ozerhamza.xyz/api/Customers
    //http://omerahiskali.com.tr/WebServiceDeneme/insert_kisiler.php
    val BASE_URL="https://ozerhamza.xyz"
    val BASE_URL2="http://omerahiskali.com.tr"
    val imageBaseUrl="https://ozerhamza.com.tr/img/"
    val auth =FirebaseAuth.getInstance()
    val database =FirebaseFirestore.getInstance()
    var customerId=0
    val gso =GoogleSignInOptions.Builder().requestIdToken("1035051567451-cl64127icla2au8q56m1nsltm20p55hs.apps.googleusercontent.com").requestEmail().build()

    fun internetControl(activity: Activity): Boolean {
        val connectivityManager=activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities= connectivityManager.activeNetwork?: return false
        val actNw=connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}