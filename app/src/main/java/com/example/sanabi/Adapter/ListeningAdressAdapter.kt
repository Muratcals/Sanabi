package com.example.sanabi.Adapter

import android.location.Address
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
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
    val addressList: ArrayList<AddressData>, val note: String
) : RecyclerView.Adapter<ListeningAdressAdapter.AddressVH>() {
    private lateinit var binding: AddressAdapterViewBinding
    val viewModel =AddressViewModel()
    val repository=Repository()
    var name =""
    var jobTick=0
    var homeTick=0
    var partnerTick=0
    class AddressVH(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.address_adapter_view,parent,false)
        binding = AddressAdapterViewBinding.bind(layoutInflater)
        return AddressVH(binding.root)
    }

    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        if (addressList[position].name.equals("Ev")) {
            name="Ev"
            binding.addressIcon.setImageResource(R.drawable.home_icon)
            binding.addressIcon.updatePadding(15,15,15,15)
            binding.addressIconText.setText("Ev")
        }
        else if (addressList[position].name.equals("İş")) {
            name="İş"
            binding.addressIcon.setImageResource(R.drawable.work_icon)
            binding.addressIcon.updatePadding(15,15,15,15)
            binding.addressIconText.setText("İş")
        }
        else if (addressList[position].name.equals("Eş")) {
            name="Eş"
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
            textAdress(holder.itemView,addressList[position])
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

    fun textAdress(view:View,adress:AddressData) {
        val bottomSheetDialog = BottomSheetDialog(view.context)
        val layoutInflater =LayoutInflater.from(view.context)
        val dialogViewBinding = SelectedAdressViewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogViewBinding.root)
        bottomSheetDialog.show()
        dialogViewBinding.lastText.setText("${adress.neighbourhood} ${adress.street} ${adress.buildingNo}")
        dialogViewBinding.secText.setText("${adress.districte} ${adress.province?:"İstanbul"} ${adress.postCode}")
        dialogViewBinding.postCode.setText(adress.postCode.toString())
        dialogViewBinding.neighbourhoodText.setText(adress.neighbourhood)
        dialogViewBinding.districteText.setText(adress.districte)
        dialogViewBinding.buildingNoText.setText(adress.buildingNo.toString())
        dialogViewBinding.streetText.setText(adress.street)
        dialogViewBinding.apartmentNumberText.setText(adress.apartmentNumber.toString())
        dialogViewBinding.phoneNumberText.setText(adress.numberPhone)
        dialogViewBinding.addresDetails.setText(adress.adressDetails)
        if (adress.name.equals("Ev")) {
            jobTick=0
            homeTick=1
            partnerTick=0
        }else if (adress.name.equals("İş")){
            jobTick=1
            homeTick=0
            partnerTick=0
        }else if (adress.name.equals("Eş")){
            jobTick=0
            homeTick=0
            partnerTick=1
        }
        updateIcon(dialogViewBinding)
        dialogViewBinding.save.setOnClickListener {
            if (dialogViewBinding.postCode.text!!.isNotEmpty() && dialogViewBinding.apartmentNumberText.text!!.isNotEmpty()
                && dialogViewBinding.districteText.text!!.isNotEmpty() && dialogViewBinding.buildingNoText.text!!.isNotEmpty()
                && dialogViewBinding.neighbourhoodText.text!!.isNotEmpty() && dialogViewBinding.streetText.text!!.isNotEmpty()
                && dialogViewBinding.addresDetails.text!!.isNotEmpty() && dialogViewBinding.phoneNumberText.text!!.isNotEmpty()
            ) {
                val apartmentNumber =Integer.parseInt(dialogViewBinding.apartmentNumberText.text.toString())
                val address =AddressData(apartmentNumber,dialogViewBinding.buildingNoText.text.toString().toInt(),
                    "${LocalDate.now().toString()} ${LocalTime.now().hour.toString()}:${LocalTime.now().minute.toString()}",util.customerId,
                dialogViewBinding.districteText.text.toString(),adress.id,name,dialogViewBinding.neighbourhoodText.text.toString(),dialogViewBinding.postCode.text.toString().toInt(),
                "İstanbul",dialogViewBinding.streetText.text.toString(),dialogViewBinding.addresDetails.text.toString(),dialogViewBinding.phoneNumberText.text.toString())
                val result =repository.updateAddress(address)
                result.enqueue(object:Callback<AddressData>{
                    override fun onResponse(
                        call: Call<AddressData>,
                        response: Response<AddressData>
                    ) {
                        println(util.customerId)
                        Toast.makeText(view.context, "Güncellendi", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                        bottomSheetDialog.cancel()
                    }

                    override fun onFailure(call: Call<AddressData>, t: Throwable) {
                       println(t.localizedMessage)
                    }
                })
            } else {
                Toast.makeText(dialogViewBinding.root.context,"Lütfen bütün alanları eksiksiz doldurun...", Toast.LENGTH_SHORT).show()
            }
        }
        dialogViewBinding.home.setOnClickListener {
            if (homeTick==1){
                name=""
                jobTick=0
                homeTick=0
                partnerTick=0
                updateIcon(dialogViewBinding)
            }else{
                name="Ev"
                jobTick=0
                homeTick=1
                partnerTick=0
                updateIcon(dialogViewBinding)
            }

        }
        dialogViewBinding.job.setOnClickListener {
           if (jobTick==1){
               name=""
               jobTick=0
               homeTick=0
               partnerTick=0
               updateIcon(dialogViewBinding)
           }else{
               name="İş"
               jobTick=1
               homeTick=0
               partnerTick=0
               updateIcon(dialogViewBinding)
           }
        }
        dialogViewBinding.partner.setOnClickListener {
            if (partnerTick==1){
                name=""
                jobTick=0
                homeTick=0
                partnerTick=0
                updateIcon(dialogViewBinding)
            }else{
                name="Eş"
                jobTick=0
                homeTick=0
                partnerTick=1
                updateIcon(dialogViewBinding)
            }

        }
    }
    fun updateIcon(viewBinding:SelectedAdressViewBinding){
        if (homeTick==1){
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home)
            viewBinding.homeIcon.updatePadding(30,30,30,30)
        }else{
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home_icon)
            viewBinding.homeIcon.updatePadding(30,30,30,30)
        }
        if (jobTick==1){
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work)
            viewBinding.jobIcon.updatePadding(30,30,30,30)
        }else{
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work_icon)
            viewBinding.jobIcon.updatePadding(30,30,30,30)
        }
        if (partnerTick==1){
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart)
            viewBinding.partnerIcon.updatePadding(30,30,30,30)
        }else{
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart_icon)
            viewBinding.partnerIcon.updatePadding(30,30,30,30)
        }
    }
}