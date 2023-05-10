package com.example.sanabi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "product")
data class BasketProductModelData(
    val categoryId: Int,
    val createdDate: String,
    val description: String,
    val amount:Int,
    @PrimaryKey val id: Int,
    val image: String,
    val name: String,
    val price: Double,
    val stock: Int
)