package com.example.sanabi.API

import com.example.sanabi.Util.util.BASE_URL
import com.example.sanabi.Util.util.BASE_URL2
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSocketFactory

class APIServices {
    fun RetrofitServices()=Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(RetrofitAPI::class.java)
}