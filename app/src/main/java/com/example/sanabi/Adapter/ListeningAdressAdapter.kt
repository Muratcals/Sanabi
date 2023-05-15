package com.example.sanabi.Adapter

import android.location.Address
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sanabi.API.Repository
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.AddressAdapterViewBinding
import com.example.sanabi.databinding.SelectedAdressViewBinding
import com.example.sanabi.model.AddressData
import com.example.sanabi.model.GetIdModel
import com.example.sanabi.view.AddressViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class ListeningAdressAdapter(
    val addressList: ArrayList<AddressData>
) : RecyclerView.Adapter<ListeningAdressAdapter.AddressVH>() {
    private lateinit var binding: AddressAdapterViewBinding
    val viewModel =AddressViewModel()
    val repository=Repository()
    class AddressVH(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.address_adapter_view,parent,false)
        binding = AddressAdapterViewBinding.bind(layoutInflater)
        return AddressVH(binding.root)
    }

    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        if (addressList[position].name.equals("Ev")) {
            binding.addressIcon.setImageResource(R.drawable.home_icon)
            binding.addressIcon.updatePadding(15,15,15,15)
            binding.addressIconText.setText("Ev")
        }
        else if (addressList[position].name.equals("İş")) {
            binding.addressIcon.setImageResource(R.drawable.work_icon)
            binding.addressIcon.updatePadding(15,15,15,15)
            binding.addressIconText.setText("İş")
        }
        else if (addressList[position].name.equals("Eş")) {
            binding.addressIcon.setImageResource(R.drawable.heart_icon)
            binding.addressIcon.updatePadding(15,15,15,15)
            binding.addressIconText.setText("İş")
        }else if (addressList[position].name.isEmpty()){
            binding.addressIcon.setImageResource(R.drawable.visit_icon)
            binding.addressIcon.updatePadding(10,10,10,10)
            binding.addressIconText.setText("${addressList[position].districte} ${addressList[position].street} ${addressList[position].buildingNo}")
        }
        binding.addressText.setText("${addressList[position].neighbourhood} Mah. ${addressList[position].street} No:${addressList[position].buildingNo}, ${addressList[position].districte} ${addressList[position].province} ")
        if (addressList[position].adressDetails.isEmpty()) {
            binding.addressDirection.setText("Adres Tarifi:Yok")
        } else {
            binding.addressDirection.setText("Adres Tarifi:${addressList[position].adressDetails}")
        }
        binding.editImage.setOnClickListener {
            val bundle = bundleOf("addressId" to addressList[position].id)
            holder.itemView.findNavController().navigate(R.id.action_addressFragment_to_selectedAdressViewFragment,bundle)
        }
        binding.deleteImage.setOnClickListener {
            val repo =repository.deleteAddress(addressList[position].id)
            repo.enqueue(object :Callback<AddressData>{
                override fun onResponse(call: Call<AddressData>, response: Response<AddressData>) {
                    Toast.makeText(holder.itemView.context, "Silindi", Toast.LENGTH_SHORT).show()
                    viewModel.getAdress()
                    notifyDataSetChanged()
                }
                override fun onFailure(call: Call<AddressData>, t: Throwable) {
                    println(t.localizedMessage)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }
}