package com.example.sanabi.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.R
import com.example.sanabi.model.AddressModel

class MainAddressControlRecycler(var addressList:AddressModel):RecyclerView.Adapter<MainAddressControlRecycler.AddressVH>() {
    var selectedAdress=0
    class AddressVH(view: View):RecyclerView.ViewHolder(view) {

        val openAddress=view.findViewById<TextView>(R.id.openAddressText)
        val openAddressDetails =view.findViewById<TextView>(R.id.openAddressDetails)
        val radioButton=view.findViewById<RadioButton>(R.id.adressSelectedRadioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.selected_adress_recycler_view,parent,false)
        return AddressVH(view)
    }

    override fun getItemCount(): Int {
        return addressList.data.size
    }

    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        holder.radioButton.isChecked = selectedAdress==addressList.data[position].id
        holder.openAddress.setText("${addressList.data[position].neighbourhood} Mah. ${addressList.data[position].street} No:${addressList.data[position].buildingNo}, ${addressList.data[position].districte} ${addressList.data[position].province} ")
        holder.openAddressDetails.setText("${addressList.data[position].districte} ${addressList.data[position].street} ${addressList.data[position].buildingNo}")
        holder.radioButton.setOnClickListener {
            update(addressList.data[position].id)
        }
    }
    fun update(id:Int){
        println(selectedAdress)
        selectedAdress=id
        println(selectedAdress)
        notifyDataSetChanged()
    }

    fun updateData(newList:AddressModel){
        addressList=newList
        notifyDataSetChanged()
    }
}