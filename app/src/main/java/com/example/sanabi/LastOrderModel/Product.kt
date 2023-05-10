package com.example.sanabi.LastOrderModel

data class Product(
    val orderId: Int,
    val product: ProductData,
    val productId: Int,
    val productQuantity: Int
)