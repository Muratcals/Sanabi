package com.example.sanabi.Adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.databinding.CategoryViewBinding
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.model.CategoryModelData
import de.hdodenhof.circleimageview.CircleImageView

class MainCategoryRecycler(var categoryList: CategoryModel): RecyclerView.Adapter<MainCategoryRecycler.CategoryVH>() {
    class CategoryVH (view: View):RecyclerView.ViewHolder(view){
        val categoryText=view.findViewById<TextView>(R.id.categoryText)
        val categoryImage=view.findViewById<CircleImageView>(R.id.categoryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val layoutInflater=LayoutInflater.from(parent.context).inflate(R.layout.category_view,parent,false)
        return CategoryVH(layoutInflater)
    }

    override fun getItemCount(): Int {
        return categoryList.data.size
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
         holder.categoryImage.downloadImage(categoryList.data[position].image)
        holder.categoryText.setText(categoryList.data[position].name)
        holder.categoryImage.setOnClickListener {
            val bundle = bundleOf("id" to categoryList.data[position].id)
            holder.itemView.findNavController().navigate(R.id.action_productMainFragment_to_categoryContentFragment,bundle)
        }
    }

    fun updateData(newData: CategoryModel) {
        categoryList = newData
        notifyDataSetChanged()
    }
}