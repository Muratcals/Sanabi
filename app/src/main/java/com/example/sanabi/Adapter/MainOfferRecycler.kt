package com.example.sanabi.Adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.Util.deneme
import com.example.sanabi.Util.downloadImage
import com.example.sanabi.databinding.OfferViewBinding

class MainOfferRecycler(val offerList:ArrayList<String>): RecyclerView.Adapter<MainOfferRecycler.OfferVH>() {
    private lateinit var binding:OfferViewBinding
    class OfferVH(view:View):RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferVH {
        val layoutInflater =LayoutInflater.from(parent.context)
        binding=OfferViewBinding.inflate(layoutInflater,parent,false)
        return OfferVH(binding.root)
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    override fun onBindViewHolder(holder: OfferVH, position: Int) {
        binding.offerImage.deneme(offerList[position])
    }
}