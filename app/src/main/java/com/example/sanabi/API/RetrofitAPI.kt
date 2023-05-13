package com.example.sanabi.API

import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.LastOrderModel.OrderProductModelItem
import com.example.sanabi.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RetrofitAPI {

    //https://ozerhamza.xyz/api/Customers

    @GET("api/Customers")
    suspend fun getData():Response<UserInformation>
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/Customers")
    fun postUserData(@Body user:Data
    ): Call<Data>

    @DELETE("api/Customers/{id}")
    suspend fun deleteUser(@Path("id") id :Int):Response<Data>

    @Headers(
        "Content-Type: application/json"
    )
    @PUT("api/Customers/")
    fun updateUser(@Body user:Data):Call<Data>

    @GET("api/Customers/GetIdWithMail")
    fun getFindById(@Query("mail") mail:String):Call<GetIdModel>

    @GET("api/Customers/{id}")
    @Headers("Content-Type: application/json")
    fun getIdData(@Path("id") id :Int):Call<GetUserInformation>

    @POST("api/Adress/")
    fun saveAddress(@Body data:AddressData):Call<AddressData>

    @GET("/api/Adress/GetSingleAdressByIdCustomerAdress/{customerId}")
    fun getSelectedAddressData(@Path("customerId") id :Int):Call<AddressModel>

    @GET("/api/Adress/{id}")
    fun getAdress(@Path("id") id:Int):Call<GetAddressModel>

    @Headers(
        "Content-Type: application/json"
    )
    @PUT("api/Adress")
    fun updateAddress(@Body address:AddressData):Call<AddressData>

    @GET("api/Order")
    fun getOrder():Call<OrderProductModel>

    @POST("api/Order")
    fun postNewOrder(@Body order:CreateOrderModel):Call<CreateOrderModel>

    @GET("api/Order/GetOrdersById")
    fun getOrderContent(@Query("selectId") id:Int):Call<OrderProductModel>

    @DELETE("api/Adress/{id}")
    fun deleteAddress(@Path("id") id:Int):Call<AddressData>

    @GET("api/Category")
    suspend fun getAllCategory():Response<CategoryModel>

    @GET("api/Order/{id}")
    fun getCustomerOrders(@Path("id") id :Int):Call<OrderProductModel>

    @GET("api/PaymentType")
    fun getAllPaymentType():Call<PaymentTypeModel>

    @GET("api/PaymentType")
    fun getPaymentType(@Path("id") id :Int):Call<GetPaymentType>
    @GET("api/Category/{id}")
    fun getCategory(@Path("id") id:Int):Call<SearchGetCategoryModel>

    @GET("api/Products")
    fun getProduct():Call<ProductModel>

    @GET("api/Category/GetSingleCategoryByIdWithProducts/{categoryId}")
    fun getCategoryProduct(@Path("categoryId") categoryId:Int):Call<GetCategoryProductsModel>

}