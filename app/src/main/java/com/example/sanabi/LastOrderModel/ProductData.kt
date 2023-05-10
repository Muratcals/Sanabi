package com.example.sanabi.LastOrderModel

data class ProductData(
    val categoryId: Int,
    val createdDate: Any,
    val description: String,
    val id: Int,
    val image: String,
    val name: String,
    val price: Double,
    val stock: Int
)