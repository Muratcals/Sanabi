package com.example.sanabi.view

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import com.example.sanabi.API.Repository
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentSelectedAdressViewBinding
import com.example.sanabi.model.AddressData
import com.example.sanabi.model.GetAddressModel
import com.example.sanabi.viewModel.SelectedAdressViewViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectedAdressViewFragment : Fragment() {

    private lateinit var viewModel: SelectedAdressViewViewModel
    private lateinit var binding:FragmentSelectedAdressViewBinding
    val repository=Repository()
    var homeTick = 0
    var partnerTick = 0
    var jobTick = 0
    var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSelectedAdressViewBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelectedAdressViewViewModel::class.java)
        arguments?.let {
            val addressId=it.getInt("addressId")
            val address =repository.getAddressInformation(addressId)
            address.enqueue(object : Callback<GetAddressModel>{
                override fun onResponse(call: Call<GetAddressModel>, response: Response<GetAddressModel>) {
                    textAdress(response.body()!!.data)
                }

                override fun onFailure(call: Call<GetAddressModel>, t: Throwable) {
                    println(t.localizedMessage)
                }
            })
        }
    }
    fun textAdress(adress: AddressData) {
        binding.lastText.setText("${adress.neighbourhood} ${adress.street} ${adress.buildingNo}")
        binding.secText.setText("${adress.districte} ${adress.province ?: "İstanbul"} ${adress.postCode}")
        binding.postCode.setText(adress.postCode.toString())
        binding.neighbourhoodText.setText(adress.neighbourhood)
        binding.districteText.setText(adress.districte)
        binding.buildingNoText.setText(adress.buildingNo.toString())
        binding.streetText.setText(adress.street)
        binding.apartmentNumberText.setText(adress.apartmentNumber.toString())
        binding.phoneNumberText.setText(adress.numberPhone)
        binding.addresDetails.setText(adress.adressDetails)
        if (adress.name.equals("Ev")) {
            jobTick = 0
            homeTick = 1
            partnerTick = 0
        } else if (adress.name.equals("İş")) {
            jobTick = 1
            homeTick = 0
            partnerTick = 0
        } else if (adress.name.equals("Eş")) {
            jobTick = 0
            homeTick = 0
            partnerTick = 1
        }
        updateIcon(binding)
        binding.save.setOnClickListener {
            if (binding.postCode.text!!.isNotEmpty() && binding.apartmentNumberText.text!!.isNotEmpty()
                && binding.districteText.text!!.isNotEmpty() && binding.buildingNoText.text!!.isNotEmpty()
                && binding.neighbourhoodText.text!!.isNotEmpty() && binding.streetText.text!!.isNotEmpty()
                && binding.addresDetails.text!!.isNotEmpty() && binding.phoneNumberText.text!!.isNotEmpty()
            ) {
                val apartmentNumber =
                    Integer.parseInt(binding.apartmentNumberText.text.toString())
                val address =
                    AddressData(
                        apartmentNumber,
                        binding.buildingNoText.text.toString().toInt(),
                        "${adress.createDate}",
                        util.customerId,
                        binding.districteText.text.toString(),
                        adress.id,
                        name,
                        binding.neighbourhoodText.text.toString(),
                        binding.postCode.text.toString().toInt(),
                        "İstanbul",
                        binding.streetText.text.toString(),
                        binding.addresDetails.text.toString(),
                        binding.phoneNumberText.text.toString()
                    )
                val result = repository.updateAddress(address)
                result.enqueue(object : Callback<AddressData> {
                    override fun onResponse(
                        call: Call<AddressData>,
                        response: Response<AddressData>
                    ) {
                        println(util.customerId)
                        Toast.makeText(requireContext(), "Güncellendi", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }

                    override fun onFailure(call: Call<AddressData>, t: Throwable) {
                        println(t.localizedMessage)
                    }
                })
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Lütfen bütün alanları eksiksiz doldurun...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.home.setOnClickListener {
            if (homeTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(binding)
            } else {
                name = "Ev"
                jobTick = 0
                homeTick = 1
                partnerTick = 0
                updateIcon(binding)
            }

        }
        binding.job.setOnClickListener {
            if (jobTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(binding)
            } else {
                name = "İş"
                jobTick = 1
                homeTick = 0
                partnerTick = 0
                updateIcon(binding)
            }
        }
        binding.partner.setOnClickListener {
            if (partnerTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(binding)
            } else {
                name = "Eş"
                jobTick = 0
                homeTick = 0
                partnerTick = 1
                updateIcon(binding)
            }
        }
    }

    fun updateIcon(viewBinding: FragmentSelectedAdressViewBinding) {
        if (homeTick == 1) {
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home)
            viewBinding.homeIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home_icon)
            viewBinding.homeIcon.updatePadding(30, 30, 30, 30)
        }
        if (jobTick == 1) {
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work)
            viewBinding.jobIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work_icon)
            viewBinding.jobIcon.updatePadding(30, 30, 30, 30)
        }
        if (partnerTick == 1) {
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart)
            viewBinding.partnerIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart_icon)
            viewBinding.partnerIcon.updatePadding(30, 30, 30, 30)
        }
    }

    fun saveData(dialog: BottomSheetDialog, item: AddressData) {
        val result = repository.postAddress(item)
        result.enqueue(object : Callback<AddressData> {
            override fun onResponse(call: Call<AddressData>, response: Response<AddressData>) {
                dialog.cancel()
                Toast.makeText(requireContext(), "Adres Eklendi", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }

            override fun onFailure(call: Call<AddressData>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
                println(t.localizedMessage)
            }

        })
    }

    fun getAddress(latLng: LatLng):List<Address>? {
        val geocoder = Geocoder(requireContext())
        try {
            val adress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
            if (adress!!.isNotEmpty()) {
                return adress
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return null
    }
}