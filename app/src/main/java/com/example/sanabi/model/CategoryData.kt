package com.example.sanabi.model

data class CategoryData(
    val createDate: String,
    val id: Int,
    val image: String,
    val name: String,
    val products: List<CategoryProducts>
)