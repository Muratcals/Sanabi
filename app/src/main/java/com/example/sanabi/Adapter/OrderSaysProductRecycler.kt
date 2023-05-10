package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.model.CategoryProducts
import com.example.sanabi.viewModel.NewOrderCreateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OrderSaysProductRecycler(var itemSays:List<CategoryProducts>,val newOrderCreateViewModel: NewOrderCreateViewModel):RecyclerView.Adapter<OrderSaysProductRecycler.SaysVH>() {
    class SaysVH(view: View):RecyclerView.ViewHolder(view) {

        val db=DatabaseRoom.getDatabase(view.context).roomDb()
        val saysImage =view.findViewById<ImageView>(R.id.saysProductImage)
        val saysName=view.findViewById<TextView>(R.id.saysProductName)
        val saysPrice=view.findViewById<TextView>(R.id.saysProductPrice)
        val saysAddOther=view.findViewById<ImageView>(R.id.saysAddOther)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaysVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.order_says,parent,false)
        return SaysVH(view)
    }

    override fun getItemCount(): Int {
        return itemSays.size
    }

    override fun onBindViewHolder(holder: SaysVH, position: Int) {
        holder.saysName.setText(itemSays[position].name)
        holder.saysImage.downloadImage(itemSays[position].image)
        holder.saysPrice.setText(itemSays[position].price.toString())
        holder.saysAddOther.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                val itemControls =holder.db.getProductById(itemSays[position].id)
                if (itemControls!=null){
                    newOrderCreateViewModel.updateOrder(itemControls.copy(amount = itemControls.amount+1))
                }else{
                    val basketProductModel = BasketProductModelData(
                        itemSays[position].categoryId,
                        "boş",
                        itemSays[position].description,
                        1,
                        itemSays[position].id,
                        itemSays[position].image,
                        itemSays[position].name,
                        itemSays[position].price,
                        itemSays[position].stock
                    )
                    newOrderCreateViewModel.addOrder(basketProductModel)
                }
            }
        }
    }

    fun updateData(newItemSays: List<CategoryProducts>){
        itemSays=newItemSays
        notifyDataSetChanged()
    }


}