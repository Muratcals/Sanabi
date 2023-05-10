package com.example.sanabi.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryContentViewModel : ViewModel() {

    val categorys=MutableLiveData<CategoryModel>()
    val categorySearch=MutableLiveData<SearchGetCategoryModel>()
    val categoryProducts=MutableLiveData<GetCategoryProductsModel>()
    val progress=MutableLiveData<Boolean>()
    val repository=Repository()
    private lateinit var roomDatabase: RoomServices
    fun getAllCategorys(){
        viewModelScope.launch(Dispatchers.Main) {
            val repo =repository.getAllCategory()
            if (repo.isSuccessful){
                categorys.value=repo.body()
            }else{
                println(repo.errorBody().toString())
            }
        }
    }

    fun getCategory(categoryId: Int){
        val product =repository.getCategory(categoryId)
        product.enqueue(object :Callback<SearchGetCategoryModel>{
            override fun onResponse(call: Call<SearchGetCategoryModel>, response: Response<SearchGetCategoryModel>) {
                if (response.isSuccessful){
                    categorySearch.value=response.body()
                }
            }
            override fun onFailure(call: Call<SearchGetCategoryModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
    }

    fun addBasketProduct(view:View,items: BasketProductModelData){
        viewModelScope.launch(Dispatchers.IO) {
            roomDatabase=DatabaseRoom.getDatabase(view.context)
            roomDatabase.roomDb().insertBasket(items)
        }
    }

    fun getAllCategoryProducts(categoryId:Int){
        progress.value=true
        val product =repository.getCategoryProducts(categoryId)
        product.enqueue(object :Callback<GetCategoryProductsModel>{
            override fun onResponse(call: Call<GetCategoryProductsModel>, response: Response<GetCategoryProductsModel>) {
                categoryProducts.value=response.body()!!
                progress.value=false
            }
            override fun onFailure(call: Call<GetCategoryProductsModel>, t: Throwable) {
                println(t.localizedMessage)
                progress.value=false
            }
        })
    }
}