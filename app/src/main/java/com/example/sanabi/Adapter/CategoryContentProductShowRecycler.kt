package com.example.sanabi.Adapter

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.model.GetCategoryProductsModel
import com.example.sanabi.viewModel.CategoryContentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CategoryContentProductShowRecycler(
    var productsItem: GetCategoryProductsModel,
    val viewModel: CategoryContentViewModel,
    val activity: FragmentActivity
) : RecyclerView.Adapter<CategoryContentProductShowRecycler.ProductVH>() {
    class ProductVH(view: View) : RecyclerView.ViewHolder(view) {

        val productName = view.findViewById<TextView>(R.id.productName)
        val productImage = view.findViewById<ImageView>(R.id.productImage)
        val productPrice = view.findViewById<TextView>(R.id.productPrice)
        val addOther = view.findViewById<ImageView>(R.id.addOther)
        val productAddOther = view.findViewById<ImageView>(R.id.productAddOther)
        val addView = view.findViewById<ConstraintLayout>(R.id.addProductView)
        val viewDeleteProduct = view.findViewById<ImageView>(R.id.deleteSubtract)
        val viewBinProduct = view.findViewById<ImageView>(R.id.deleteBin)
        val viewAmountText = view.findViewById<TextView>(R.id.productAmount)
        val database = DatabaseRoom.getDatabase(view.context).roomDb()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_view, parent, false)
        return ProductVH(view)
    }

    override fun getItemCount(): Int {
        return productsItem.data.products.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        val count =count(holder)
        holder.productName.setText(productsItem.data.products[position].description)
        holder.productImage.downloadImage(productsItem.data.products[position].image)
        holder.productPrice.setText("${productsItem.data.products[position].price} TL")
        holder.addOther.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                val itemControl = holder.database
                    .getProductById(productsItem.data.products[position].id)
                if (itemControl == null) {
                    val basketProductModel = BasketProductModelData(
                        productsItem.data.products[position].categoryId,
                        "boş",
                        productsItem.data.products[position].description,
                        1,
                        productsItem.data.products[position].id,
                        productsItem.data.products[position].image,
                        productsItem.data.products[position].name,
                        productsItem.data.products[position].price,
                        productsItem.data.products[position].stock
                    )
                    addBasketItems(holder.itemView, basketProductModel)
                    activity.runOnUiThread {
                        holder.viewAmountText.setText("1")
                        holder.viewBinProduct.visibility = View.VISIBLE
                        holder.viewDeleteProduct.visibility = View.GONE
                        Toast.makeText(
                            holder.itemView.context,
                            "${productsItem.data.products[position].name} sepete eklendi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                   showAddView(holder, itemControl)
                    count.start()
                }
            }
        }
        holder.productAddOther.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                val itemControl = holder.database
                    .getProductById(productsItem.data.products[position].id)
                holder.database
                    .updateBasket(itemControl!!.copy(amount = itemControl.amount + 1))
                activity.runOnUiThread {
                    holder.viewAmountText.setText((itemControl.amount + 1).toString())
                    Toast.makeText(
                        holder.itemView.context,
                        "${itemControl.name} sepete eklendi",
                        Toast.LENGTH_SHORT
                    ).show()
                    count.cancel()
                    count.start()
                }
            }
        }
        holder.viewDeleteProduct.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
            val itemControl = holder.database
                .getProductById(productsItem.data.products[position].id)
                holder.database.updateBasket(itemControl!!.copy(amount = itemControl.amount - 1))
                activity.runOnUiThread {
                    holder.viewAmountText.setText((itemControl.amount - 1).toString())
                }
            }
            count.cancel()
            count.start()
        }
        holder.viewBinProduct.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                holder.database.deleteBaskets(productsItem.data.products[position].id)
                activity.runOnUiThread {
                    holder.viewAmountText.setText("0")
                    count.cancel()
                    count.start()
                }
            }
        }
    }

    fun updateData(newData: GetCategoryProductsModel) {
        productsItem = newData
        notifyDataSetChanged()
    }

    fun addBasketItems(
        view: View,
        items: BasketProductModelData
    ) {
        viewModel.addBasketProduct(view, items)
    }

    fun showAddView(holder: ProductVH, itemControl: BasketProductModelData) {
        activity.runOnUiThread {
            if (itemControl.amount>1){
                holder.viewBinProduct.visibility = View.GONE
                holder.viewDeleteProduct.visibility = View.VISIBLE
            }else{
                holder.viewBinProduct.visibility = View.VISIBLE
                holder.viewDeleteProduct.visibility = View.GONE
            }
            holder.viewAmountText.setText((itemControl.amount).toString())
        }
    }

    fun count(holder:ProductVH):CountDownTimer{
        return object : CountDownTimer(3500, 1000) {
            override fun onTick(p0: Long) {
                holder.addOther.visibility = View.GONE
                holder.addView.visibility = View.VISIBLE
            }
            override fun onFinish() {
                holder.addOther.visibility = View.VISIBLE
                holder.addView.visibility = View.GONE
            }
        }
    }
}