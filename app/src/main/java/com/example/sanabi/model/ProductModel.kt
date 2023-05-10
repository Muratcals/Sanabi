package com.example.sanabi.model

import androidx.room.Entity

data class ProductModel(
    val data: List<ProductModelData>,
    val errors: String
)