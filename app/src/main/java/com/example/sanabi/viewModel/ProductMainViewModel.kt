package com.example.sanabi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanabi.API.Repository
import com.example.sanabi.model.CategoryModel
import kotlinx.coroutines.launch

class ProductMainViewModel : ViewModel() {
    val categoryModel=MutableLiveData<CategoryModel>()
    val repository=Repository()
    val progress=MutableLiveData<Boolean>()
    fun getCategory(){
        progress.value=true
        viewModelScope.launch {
            val result =repository.getAllCategory()
            if (result.isSuccessful){
                categoryModel.value=result.body()
                progress.value=false
            }else{
                println(result.errorBody().toString())
                progress.value=false
            }
        }
    }
}