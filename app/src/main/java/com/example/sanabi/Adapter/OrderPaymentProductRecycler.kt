package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.Util.decimalFormet
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.viewModel.OrderPaymentViewModel

class OrderPaymentProductRecycler(var orderList:ArrayList<BasketProductModelData>, val viewModelOrderPaymentViewModel: OrderPaymentViewModel):RecyclerView.Adapter<OrderPaymentProductRecycler.PaymentProductVH>() {
    class PaymentProductVH(view: View):RecyclerView.ViewHolder(view) {
        val paymentProductName=view.findViewById<TextView>(R.id.paymentProductName)
        val paymentProductPrice=view.findViewById<TextView>(R.id.paymentProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentProductVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.payment_recycler_view,parent,false)
        return PaymentProductVH(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: PaymentProductVH, position: Int) {
        holder.paymentProductName.setText("${orderList[position].amount}x ${orderList[position].name}")
        val productSumPrice=orderList[position].price * orderList[position].amount
        holder.paymentProductPrice.decimalFormet(productSumPrice)
    }

    fun updateData(list:List<BasketProductModelData>){
        orderList.clear()
        orderList= list as ArrayList<BasketProductModelData>
        notifyDataSetChanged()
    }
}