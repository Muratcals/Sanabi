package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.model.CategoryModelData
import com.example.sanabi.viewModel.CategoryContentViewModel

class MainDetailsCategoryNameRecycler(var category:List<CategoryModelData>, var id :Int,val viewModel:CategoryContentViewModel):RecyclerView.Adapter<MainDetailsCategoryNameRecycler.CategoryVH>() {

    class CategoryVH(view:View) :RecyclerView.ViewHolder(view) {

            val name =view.findViewById<TextView>(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val layoutInflater =LayoutInflater.from(parent.context).inflate(R.layout.category_recycler_view,parent,false)
        return CategoryVH(layoutInflater)
    }

    override fun getItemCount(): Int {
        return category.size
    }
    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        if (id==category[position].id){
            holder.name.setTextColor(holder.itemView.resources.getColor(R.color.pink))
        }else{
            holder.name.setTextColor(holder.itemView.resources.getColor(R.color.black))
        }
        holder.name.setOnClickListener {
            id=category[position].id
            notifyDataSetChanged()
            viewModel.getAllCategoryProducts(category[position].id)
        }
        holder.name.setText(category[position].name)
    }


    fun updateData(newData: List<CategoryModelData>,getId: Int) {
        category = newData
        id=getId
        notifyDataSetChanged()
    }
}