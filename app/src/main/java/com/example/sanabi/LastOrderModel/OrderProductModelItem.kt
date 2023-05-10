package com.example.sanabi.LastOrderModel

data class OrderProductModelItem(
    val _Products: List<Product>,
    val adressId: Int,
    val amount: Double,
    val createDate: String,
    val customer: Customer,
    val customerId: Int,
    val id: Int,
    val orderStatus: OrderStatus,
    val orderStatusId: Int,
    val paymentType: PaymentType,
    val paymentTypeId: Int
)