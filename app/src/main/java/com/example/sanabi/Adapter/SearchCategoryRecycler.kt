package com.example.sanabi.Adapter

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.MainActivity
import com.example.sanabi.MainDetailsActivity
import com.example.sanabi.R
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.model.CategoryData
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.model.CategoryProducts

class SearchCategoryRecycler(val categoryList:CategoryModel):RecyclerView.Adapter<SearchCategoryRecycler.SearchCategoryVH>() {
    class SearchCategoryVH(view: View):RecyclerView.ViewHolder(view) {
        val categoryImage =view.findViewById<ImageView>(R.id.searchCategoryImage)
        val categoryName=view.findViewById<TextView>(R.id.searchCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.search_category_recycler_view,parent,false)
        return SearchCategoryVH(view)
    }

    override fun getItemCount(): Int {
        return categoryList.data.size
    }

    override fun onBindViewHolder(holder: SearchCategoryVH, position: Int) {
        holder.categoryImage.downloadImage(categoryList.data[position].image)
        holder.categoryName.setText(categoryList.data[position].name)
        holder.categoryName.setOnClickListener {
            val intent =Intent(holder.itemView.context,MainDetailsActivity::class.java)
            intent.putExtra("incoming","search")
            intent.putExtra("categoryId",categoryList.data[position].id)
            startActivity(holder.itemView.context,intent,null)
        }
    }

}