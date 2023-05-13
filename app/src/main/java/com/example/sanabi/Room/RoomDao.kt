package com.example.sanabi.Room

import androidx.room.*
import com.example.sanabi.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM product")
    fun getAllBaskets(): Flow<List<BasketProductModelData>>
    @Query("SELECT * FROM product")
    fun getAllBasket(): List<BasketProductModelData>
    @Query("SELECT DISTINCT categoryId from product")
    fun getBasketCategoryId():List<Int>
    @Insert
    fun insertBasket(basket: BasketProductModelData)
    @Update
    fun updateBasket(basket: BasketProductModelData)

    @Query("DELETE FROM product")
    fun deleteAllBasket()
    @Query("DELETE FROM product where id=:productId")
    fun deleteBaskets(productId:Int)

    @Query("SELECT * FROM product WHERE id=:productId")
    fun getProductById(productId: Int):BasketProductModelData?

}

@Dao
interface SearchDao {
    @Query("SELECT * FROM search")
    fun getPastSearch():List<SearchProductModel>

    @Query("DELETE FROM search")
    fun deletePastSearch()

    @Insert
    fun insertPastSearch(item: SearchProductModel)

    @Query("SELECT * FROM search WHERE id=:categoryId")
    fun searchItemControl(categoryId:Int):List<SearchProductModel>
}