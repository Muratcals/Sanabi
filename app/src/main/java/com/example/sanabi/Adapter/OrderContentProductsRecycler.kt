package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.decimalFormet
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.viewModel.NewOrderCreateViewModel
import de.hdodenhof.circleimageview.CircleImageView

class OrderContentProductsRecycler(
    var orderList: List<BasketProductModelData>,
    val newOrderCreateViewModel: NewOrderCreateViewModel
) : RecyclerView.Adapter<OrderContentProductsRecycler.ProductVH>() {
    class ProductVH(view: View) : RecyclerView.ViewHolder(view) {
        val db = DatabaseRoom.getDatabase(view.context).roomDb()
        val productImage = view.findViewById<ImageView>(R.id.orderContentProductImage)
        val productName = view.findViewById<TextView>(R.id.orderContentProductName)
        val productPrice = view.findViewById<TextView>(R.id.orderContentProductPrice)
        val productCount = view.findViewById<TextView>(R.id.productCount)
        val addOrder = view.findViewById<CircleImageView>(R.id.addOrderProduct)
        val deleteOrder = view.findViewById<CircleImageView>(R.id.deleteOrderProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.order_product_view, parent, false)
        return ProductVH(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.productImage.downloadImage(orderList[position].image)
        holder.productCount.setText(orderList[position].amount.toString())
        holder.productName.setText(orderList[position].name)
        val total = (orderList[position].price * orderList[position].amount)
        holder.productPrice.decimalFormet(total)
        holder.deleteOrder.setOnClickListener {
            if (orderList[position].amount > 1) {
                newOrderCreateViewModel.updateOrder(orderList[position].copy(amount = orderList[position].amount - 1))
            } else {
                newOrderCreateViewModel.deleteOrder(orderList[position].id)
            }
        }
        holder.addOrder.setOnClickListener {
            newOrderCreateViewModel.updateOrder(orderList[position].copy(amount = orderList[position].amount + 1))
        }
    }

    fun updateData(items: List<BasketProductModelData>) {
        val diffutil = CoursesCallBack(orderList, items)
        val diffResult = DiffUtil.calculateDiff(diffutil)
        diffResult.dispatchUpdatesTo(this)
        orderList = items
    }
}