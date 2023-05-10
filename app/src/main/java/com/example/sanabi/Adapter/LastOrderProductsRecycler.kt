package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.LastOrderModel.Product
import com.example.sanabi.R
import com.example.sanabi.viewModel.LastOrderContentViewModel

class LastOrderProductsRecycler(var orderList: List<Product>, val viewModel:LastOrderContentViewModel) :
    RecyclerView.Adapter<LastOrderProductsRecycler.PaymentProductVH>() {
    class PaymentProductVH(view: View) : RecyclerView.ViewHolder(view) {
        val paymentProductAmount = view.findViewById<TextView>(R.id.paymentProductAmount)
        val paymentProductName = view.findViewById<TextView>(R.id.paymentProductName)
        val paymentProductPrice = view.findViewById<TextView>(R.id.paymentProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentProductVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_recycler_view, parent, false)
        return PaymentProductVH(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: PaymentProductVH, position: Int) {
        holder.paymentProductAmount.setText("${orderList[position].productQuantity}x ")
        holder.paymentProductName.setText("${orderList[position].product.name}")
        for (items in orderList){
            val productSumPrice = orderList[position].product.price * orderList[position].productQuantity
            holder.paymentProductPrice.setText("${viewModel.decimalFormet(productSumPrice)} TL")
        }
    }
}