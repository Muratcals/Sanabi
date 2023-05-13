package com.example.sanabi.Adapter

import android.app.Activity
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.R
import com.example.sanabi.Util.decimalFormet
import com.example.sanabi.viewModel.LastOrderViewModel
import com.google.firestore.v1.StructuredQuery.Order
import org.w3c.dom.Text

class LastOrdersRecycler(val orderList:OrderProductModel,val viewModel:LastOrderViewModel,val activity: Activity):RecyclerView.Adapter<LastOrdersRecycler.OrderVH>() {
    class OrderVH(view: View):RecyclerView.ViewHolder(view) {
        val image=view.findViewById<ImageView>(R.id.marketIcon)
        val date=view.findViewById<TextView>(R.id.date)
        val price =view.findViewById<TextView>(R.id.price)
        val infromation =view.findViewById<TextView>(R.id.lastOrderContent)
        val replaceOrderButton=view.findViewById<Button>(R.id.replaceOrderButton)
        val orderDetailsLayout=view.findViewById<LinearLayout>(R.id.orderDetailsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.last_order_view,parent,false)
        return OrderVH(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        var sumPrice=0.0
        holder.infromation.setText("")
        for (product in orderList[position]._Products){
            sumPrice+=product.product.price*product.productQuantity
            holder.infromation.append(" ${product.product.name},")
        }
        holder.price.decimalFormet(sumPrice)
        holder.date.setText(orderList[position].createDate)
        holder.replaceOrderButton.setOnClickListener {
            viewModel.addOrderBasket(activity,orderList[position]._Products)
        }
        holder.orderDetailsLayout.setOnClickListener {
            val bundle = bundleOf("orderId" to orderList[position].id)
            holder.itemView.findNavController().navigate(R.id.action_lastOrderFragment_to_lastOrderContentFragment,bundle)
        }
    }
}