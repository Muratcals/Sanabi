package com.example.sanabi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
class SearchProductModel(
    val categoryId: Int,
    val createdDate: String?,
    val description: String,
    @PrimaryKey
    val id: Int,
    val image: String,
    val name: String,
    val price: Double,
    val stock: Int
) {

}