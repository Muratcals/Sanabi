package com.example.sanabi.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.PasteSearchRoomServices
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.model.ProductModel
import com.example.sanabi.model.ProductModelData
import com.example.sanabi.model.SearchProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call

class SearchActivityViewModel:ViewModel() {

    val categoryList=MutableLiveData<CategoryModel>()
    val pastSearchList=MutableLiveData<List<SearchProductModel>>()
    val repository=Repository()
    private lateinit var searchDatabase:PasteSearchRoomServices

    fun getCategory(){
        viewModelScope.launch {
            val categoryListResult=repository.getAllCategory()
            if (categoryListResult.isSuccessful){
                if (categoryListResult.body()!!.data.isEmpty()){
                    println("bOŞ")
                }else{
                    categoryList.value=categoryListResult.body()
                }
            }else{
                println(categoryListResult.errorBody().toString())
            }
        }
    }

    fun getPasteSearch(context:Context){
        searchDatabase=DatabaseRoom.getSearchDatabase(context)
        MainScope().launch(Dispatchers.IO) {
            val list =searchDatabase.searchRoomDb().getPastSearch()
            pastSearchList.postValue(list)
        }
    }
    fun deletePastSearch(context: Context){
        searchDatabase=DatabaseRoom.getSearchDatabase(context)
        MainScope().launch(Dispatchers.IO) {
            searchDatabase.searchRoomDb().deletePastSearch()
            pastSearchList.postValue(arrayListOf())
        }
    }

    fun getSearchProduct(): Call<ProductModel> {
        return repository.getProduct()
    }

    fun pastSearchControl(){

    }
}