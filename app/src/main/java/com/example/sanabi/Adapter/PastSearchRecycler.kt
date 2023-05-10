package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.SearchActivity
import com.example.sanabi.model.ProductModelData
import com.example.sanabi.model.SearchProductModel

class PastSearchRecycler(val pastSearchList:List<SearchProductModel>,val activity: SearchActivity):RecyclerView.Adapter<PastSearchRecycler.PastSearchVH>() {
    class PastSearchVH(view:View):RecyclerView.ViewHolder(view) {
        val pastText=view.findViewById<TextView>(R.id.pastSearchText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastSearchVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.only_search_text_layout,parent,false)
        return PastSearchVH(view)
    }

    override fun getItemCount(): Int {
        return pastSearchList.size
    }

    override fun onBindViewHolder(holder: PastSearchVH, position: Int) {
        holder.pastText.setText(pastSearchList[position].name)
        holder.pastText.setOnClickListener {
            activity.postSearchProductCategory(pastSearchList[position].categoryId)
        }
    }
}