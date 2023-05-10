package com.example.sanabi.API

import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.LastOrderModel.OrderProductModelItem
import com.example.sanabi.model.*
import retrofit2.Call
import retrofit2.Response

class Repository {

    suspend fun getAllData(): Response<UserInformation> {
        return APIServices().RetrofitServices().getData()
    }
    fun postUserData(user: Data): Call<Data> {
        return APIServices().RetrofitServices().postUserData(user)
    }

    fun getIdUser(eMail:String): Call<GetIdModel>{
        return APIServices().RetrofitServices().getFindById(eMail)
    }

    fun getIdUserData(id:Int): Call<GetUserInformation>{
        return APIServices().RetrofitServices().getIdData(id)
    }

    fun updateUser(user:Data):Call<Data>{
        return APIServices().RetrofitServices().updateUser(user)
    }

    fun postAddress(item :AddressData):Call<AddressData>{
        return APIServices().RetrofitServices().saveAddress(item)
    }

    fun getSelectedAddress(id:Int):Call<AddressModel>{
        return APIServices().RetrofitServices().getSelectedAddressData(id)
    }

    fun getAddressInformation(id:Int):Call<GetAddressModel>{
        return APIServices().RetrofitServices().getAdress(id)
    }

    fun updateAddress(address:AddressData):Call<AddressData>{
        return APIServices().RetrofitServices().updateAddress(address)
    }

    fun getAllLastOrder():Call<OrderProductModel>{
        return APIServices().RetrofitServices().getOrder()
    }

    fun getLastOrderContent(id:Int):Call<OrderProductModel>{
        return APIServices().RetrofitServices().getOrderContent(id)
    }

    fun postNewOrder(order: OrderProductModelItem):Call<OrderProductModelItem>{
        return APIServices().RetrofitServices().postNewOrder(order)
    }

    fun deleteAddress(id:Int):Call<AddressData>{
        return APIServices().RetrofitServices().deleteAddress(id)
    }

    fun getCategory(categoryId: Int):Call<SearchGetCategoryModel>{
        return APIServices().RetrofitServices().getCategory(categoryId)
    }
    suspend fun getAllCategory():Response<CategoryModel>{
        return APIServices().RetrofitServices().getAllCategory()
    }

    fun getProduct():Call<ProductModel>{
        return APIServices().RetrofitServices().getProduct()
    }

    fun getCategoryProducts(categoryId:Int):Call<GetCategoryProductsModel>{
        return APIServices().RetrofitServices().getCategoryProduct(categoryId)
    }
}