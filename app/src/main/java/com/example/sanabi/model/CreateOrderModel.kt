package com.example.sanabi.model

data class CreateOrderModel(
    val _Products: List<Product>,
    val adressId: Int,
    val amount: Double,
    val createDate: String,
    val customerId: Int,
    val id: Int,
    val orderStatusId: Int,
    val paymentTypeId: Int
)