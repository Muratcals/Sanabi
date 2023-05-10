package com.example.sanabi.Util

import androidx.room.TypeConverter
import com.example.sanabi.model.BasketProductModelData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BasketProductModelDataConverter {
    @TypeConverter
    fun fromString(value: String): List<BasketProductModelData> {
        val listType = object : TypeToken<List<BasketProductModelData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<BasketProductModelData>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}