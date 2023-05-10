package com.example.sanabi.Room

import androidx.room.*
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.model.ProductModelData
import com.example.sanabi.model.SearchProductModel

@Database(entities = [BasketProductModelData::class], version = 10)
abstract class RoomServices :RoomDatabase(){
    abstract fun roomDb():RoomDao

}
@Database(entities = [SearchProductModel::class], version = 22)
abstract class PasteSearchRoomServices : RoomDatabase(){

    abstract fun searchRoomDb():SearchDao

}