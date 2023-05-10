package com.example.sanabi.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.API.Repository
import com.example.sanabi.Adapter.MainAddressControlRecycler
import com.example.sanabi.Adapter.OrderPaymentProductRecycler
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentOrderPaymentBinding
import com.example.sanabi.databinding.SelectedAdressDialogRecyclerBinding
import com.example.sanabi.model.AddressModel
import com.example.sanabi.viewModel.OrderPaymentViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderPaymentFragment : Fragment() {

    private lateinit var binding: FragmentOrderPaymentBinding
    private lateinit var viewModel: OrderPaymentViewModel
    private lateinit var adapter: OrderPaymentProductRecycler
    private lateinit var sharedPreferences: SharedPreferences
    val repository = Repository()
    val transferPrice = 15.25

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderPaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderPaymentViewModel::class.java)
        viewModel.getProduct(requireView())
        sharedPreferences =
            requireActivity().getSharedPreferences("AddressId", Context.MODE_PRIVATE)
        val addressId = sharedPreferences.getInt("addressId", 0)
        viewModel.getAddress(addressId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = OrderPaymentProductRecycler(arrayListOf(), viewModel)
        binding.paymentSummaryRecycler.adapter = adapter
        binding.paymentSummaryRecycler.layoutManager = LinearLayoutManager(requireContext())
        arguments.let {
            val productPrice = it!!.getDouble("productsPrice")
            viewModel.getProduct(requireView()).onEach {
                adapter.updateData(it)
                binding.paymentProductSumPrice.setText("${viewModel.decimalFormet(productPrice)} TL")
                binding.paymentSumPrice.setText(viewModel.decimalFormet(productPrice + transferPrice))
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        viewModel.addressInformation.observe(viewLifecycleOwner) {
            if (it.data.name.isNotEmpty()) {
                binding.paymentAddressDetails.setText(it.data.adressDetails)
            }else{
                binding.paymentAddressDetails.visibility=View.GONE
            }
            binding.paymentAddress2.setText("${it.data.neighbourhood} Mah. ${it.data.street} No:${it.data.buildingNo} ")
            binding.paymentAddress3.setText("${it.data.districte} ${it.data.province} ${it.data.postCode}")
            if (it.data.adressDetails.isEmpty()){

            }else{
                binding.paymentAddIcon.visibility=View.GONE
                binding.paymentAddressDetails.setText("${it.data.adressDetails}")
                binding.paymentAddressDetails.setTextColor(resources.getColor(com.example.sanabi.R.color.black))
            }
        }
        binding.paymentEditButton.setOnClickListener {
            selectedAdressDialog()
        }
    }

    fun selectedAdressDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = SelectedAdressDialogRecyclerBinding.inflate(layoutInflater)
        dialog.show()
        dialog.setContentView(dialogBinding.root)
        val addressList = repository.getSelectedAddress(util.customerId)
        val addressAdapter = MainAddressControlRecycler(AddressModel(arrayListOf(), ""))
        addressList.enqueue(object : Callback<AddressModel> {
            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {
                if (response.isSuccessful) {
                    addressAdapter.updateData(response.body()!!)
                    dialogBinding.customerAddressRecycler.adapter = addressAdapter
                    dialogBinding.customerAddressRecycler.layoutManager =
                        LinearLayoutManager(requireContext())
                }
            }
            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
        dialogBinding.successLocation.setOnClickListener {
            sharedPreferences =
                requireActivity().getSharedPreferences("AddressId", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (addressAdapter.selectedAdress == 0) {
                Toast.makeText(requireContext(), "Adres seçiniz", Toast.LENGTH_SHORT).show()
            } else {
                editor.putInt("addressId", addressAdapter.selectedAdress)
                editor.apply()
                dialog.cancel()
            }
            viewModel.getAddress(sharedPreferences.getInt("addressId", 0))
        }
    }
}