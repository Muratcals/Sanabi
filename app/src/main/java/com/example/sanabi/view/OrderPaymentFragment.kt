package com.example.sanabi.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.API.Repository
import com.example.sanabi.Adapter.MainAddressControlRecycler
import com.example.sanabi.Adapter.OrderPaymentProductRecycler
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentOrderPaymentBinding
import com.example.sanabi.databinding.SelectedAdressDialogRecyclerBinding
import com.example.sanabi.model.AddressModel
import com.example.sanabi.viewModel.OrderPaymentViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderPaymentViewModel::class.java)
        viewModel.getProduct(requireView())
        sharedPreferences =
            requireActivity().getSharedPreferences("AddressId", Context.MODE_PRIVATE)
        val addressId = sharedPreferences.getInt("addressId", 0)
        viewModel.getAddress(addressId)
        val sharedPreferencesPayment =requireActivity().getSharedPreferences("PaymentMethod",Context.MODE_PRIVATE)
        val paymentId=sharedPreferencesPayment.getInt("PaymentId",0)
        if (paymentId!=0){
            binding.paymentMethodLayout.visibility=View.VISIBLE
            binding.methodUpdateLayout.visibility=View.GONE
            if (paymentId==1){
                binding.paymentMethodIcon.setImageResource(R.drawable.card_black_icon)
                binding.paymentMethodText.setText("Kapıda Kredi Kartı ")
            }else{
                binding.paymentMethodIcon.setImageResource(R.drawable.purse_black_icon)
                binding.paymentMethodText.setText("Kapıda Nakit ")
            }
        }else{
            binding.paymentMethodLayout.visibility=View.GONE
            binding.methodUpdateLayout.visibility=View.VISIBLE
        }
        binding.addPaymentMethod.setOnClickListener {
            selectedPaymentDialog()
        }
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
                binding.paymentAddressDetails.setText(it.data.name)
            } else {
                binding.paymentAddressDetails.visibility = View.GONE
            }
            binding.paymentAddress2.setText("${it.data.neighbourhood} Mah. ${it.data.street} No:${it.data.buildingNo} ")
            binding.paymentAddress3.setText("${it.data.districte} ${it.data.province} ${it.data.postCode}")
            if (it.data.adressDetails.isNotEmpty()) {
                binding.paymentAddIcon.visibility = View.GONE
                binding.addPaymentAddressDetails.setText(it.data.adressDetails)
                binding.addPaymentAddressDetails.setTextColor(resources.getColor(com.example.sanabi.R.color.black))
            }
        }
        binding.paymentEditButton.setOnClickListener {
            selectedAdressDialog()
        }
        binding.addressDetailsUpdate.setOnClickListener {

        }
    }

    fun selectedPaymentDialog() {
        val sharedPreferencesPayment =
            requireActivity().getSharedPreferences("PaymentMethod", Context.MODE_PRIVATE)
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.selected_payment_method_view, null, false)
        val paymentRadioButton = view.findViewById<RadioGroup>(R.id.paymentMethodRadioGroup)
        val cashPaymentButton = view.findViewById<RadioButton>(R.id.cashPayment)
        val cardPaymentButton = view.findViewById<RadioButton>(R.id.cardPayment)
        viewModel.getPaymentType()
        viewModel.paymentType.observe(viewLifecycleOwner) {
            if (it.data[0].id == 1) {
                cardPaymentButton.setText(it.data[0].typeName)
                cashPaymentButton.setText(it.data[1].typeName)
            } else {
                cardPaymentButton.setText(it.data[1].typeName)
                cashPaymentButton.setText(it.data[0].typeName)
            }
            paymentRadioButton.setOnCheckedChangeListener(object :
                RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                    sharedPreferencesPayment.edit().apply {
                        if (p1 == R.id.cashPayment) {
                            this.putInt("paymentId", 1)
                        } else {
                            this.putInt("paymentId",2)
                        }
                    }
                }
            })
        }
        dialog.setContentView(view)
        dialog.show()
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