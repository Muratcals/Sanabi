package com.example.sanabi.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.MainDetailsActivity
import com.example.sanabi.R
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.SearchActivity
import com.example.sanabi.model.ProductModelData
import com.example.sanabi.model.SearchProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SearchProductsRecycler(val searchList: List<SearchProductModel>, val activity: SearchActivity):RecyclerView.Adapter<SearchProductsRecycler.SearchProductsVH>() {
    class SearchProductsVH(view:View):RecyclerView.ViewHolder(view) {
        val searchText=view.findViewById<TextView>(R.id.searchText)
        val pasteSearch=view.findViewById<ImageView>(R.id.pasteSearchEditText)
        val database =DatabaseRoom.getSearchDatabase(view.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductsVH {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.search_products_view,parent,false)
        return SearchProductsVH(view)
    }

    override fun getItemCount(): Int {
        if (searchList.size > 8) {
            return 8;
        } else {
            return searchList.size;
        }
    }

    override fun onBindViewHolder(holder: SearchProductsVH, position: Int) {
        holder.searchText.setText(searchList[position].name)
        holder.pasteSearch.setOnClickListener {
            activity.searchTextUpdate(searchList[position].name)
        }
        holder.searchText.setOnClickListener {
            MainScope().launch(Dispatchers.IO) {
                val control =holder.database.searchRoomDb().searchItemControl(searchList[position].id)
                if (control.isEmpty()){
                    holder.database.searchRoomDb().insertPastSearch(searchList[position])
                }
                activity.postSearchProductCategory(searchList[position].categoryId)
            }
        }
    }
}