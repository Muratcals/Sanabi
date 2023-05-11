package com.example.sanabi.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.updatePadding
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
import com.example.sanabi.databinding.SelectedAdressViewBinding
import com.example.sanabi.model.AddressData
import com.example.sanabi.model.AddressModel
import com.example.sanabi.viewModel.OrderPaymentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime


class OrderPaymentFragment : Fragment() {

    private lateinit var binding: FragmentOrderPaymentBinding
    private lateinit var viewModel: OrderPaymentViewModel
    private lateinit var adapter: OrderPaymentProductRecycler
    private lateinit var sharedPreferences: SharedPreferences
    val repository = Repository()
    var name =""
    var jobTick=0
    var homeTick=0
    var partnerTick=0
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
        paymentMehodUIUpdate()
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
                binding.paymentMethodSumPrice.setText(viewModel.decimalFormet(productPrice + transferPrice))
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
            }
        }
        binding.paymentMethodEditButton.setOnClickListener {
            selectedPaymentDialog()
        }
        binding.addressDetailsUpdate.setOnClickListener {
            viewModel.addressInformation.value?.data?.let { it1 -> textAdress(it1) }
        }
        binding.paymentAddressEditButton.setOnClickListener {
            selectedAdressDialog()
        }
    }

    fun paymentMehodUIUpdate(){
        val sharedPreferencesPayment =requireActivity().getSharedPreferences("PaymentMethod",Context.MODE_PRIVATE)
        val paymentId=sharedPreferencesPayment.getInt("paymentId",0)
        println(paymentId)
        if (paymentId!=0){
            binding.paymentMethodLayout.visibility=View.VISIBLE
            binding.methodUpdateLayout.visibility=View.GONE
            if (paymentId==1){
                binding.paymentMethodIcon.setImageResource(R.drawable.card_black_icon)
                binding.paymentMethodText.setText("Kapıda Kredi Kartı ")
            }else{
                binding.paymentMethodIcon.setImageResource(R.drawable.purse_black_icon)
                binding.paymentMethodText.setText("Nakit ")
            }
        }else{
            binding.paymentMethodLayout.visibility=View.GONE
            binding.methodUpdateLayout.visibility=View.VISIBLE
        }
    }
    fun selectedPaymentDialog() {
        val sharedPreferencesPayment =
            requireActivity().getSharedPreferences("PaymentMethod", Context.MODE_PRIVATE)
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.selected_payment_method_view, null, false)
        val paymentSelectedButton=view.findViewById<Button>(R.id.paymentDialogButton)
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
        }
        if (sharedPreferencesPayment.getInt("paymentId",0)==1){
            cardPaymentButton.isChecked=true
        }else{
            cashPaymentButton.isChecked=true
        }
        dialog.setContentView(view)
        dialog.show()
        paymentSelectedButton.setOnClickListener {
            val editor =sharedPreferencesPayment.edit()
            if (cardPaymentButton.isChecked) {
                println("id 1 ")
               editor.putInt("paymentId", 1)
            } else {
                println("id 2 ")
                editor.putInt("paymentId",2)
            }
            editor.apply()
            paymentMehodUIUpdate()
            dialog.cancel()
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

    fun textAdress(adress: AddressData) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val layoutInflater = LayoutInflater.from(requireContext())
        val dialogViewBinding = SelectedAdressViewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogViewBinding.root)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        dialogViewBinding.lastText.setText("${adress.neighbourhood} ${adress.street} ${adress.buildingNo}")
        dialogViewBinding.secText.setText("${adress.districte} ${adress.province ?: "İstanbul"} ${adress.postCode}")
        dialogViewBinding.postCode.setText(adress.postCode.toString())
        dialogViewBinding.neighbourhoodText.setText(adress.neighbourhood)
        dialogViewBinding.districteText.setText(adress.districte)
        dialogViewBinding.buildingNoText.setText(adress.buildingNo.toString())
        dialogViewBinding.streetText.setText(adress.street)
        dialogViewBinding.apartmentNumberText.setText(adress.apartmentNumber.toString())
        dialogViewBinding.phoneNumberText.setText(adress.numberPhone)
        dialogViewBinding.addresDetails.setText(adress.adressDetails)
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
        updateIcon(dialogViewBinding)
        dialogViewBinding.save.setOnClickListener {
            if (dialogViewBinding.postCode.text!!.isNotEmpty() && dialogViewBinding.apartmentNumberText.text!!.isNotEmpty()
                && dialogViewBinding.districteText.text!!.isNotEmpty() && dialogViewBinding.buildingNoText.text!!.isNotEmpty()
                && dialogViewBinding.neighbourhoodText.text!!.isNotEmpty() && dialogViewBinding.streetText.text!!.isNotEmpty()
                && dialogViewBinding.addresDetails.text!!.isNotEmpty() && dialogViewBinding.phoneNumberText.text!!.isNotEmpty()
            ) {
                val apartmentNumber =
                    Integer.parseInt(dialogViewBinding.apartmentNumberText.text.toString())
                val address =
                    AddressData(
                        apartmentNumber,
                        dialogViewBinding.buildingNoText.text.toString().toInt(),
                        "${adress.createDate}",
                        util.customerId,
                        dialogViewBinding.districteText.text.toString(),
                        adress.id,
                        name,
                        dialogViewBinding.neighbourhoodText.text.toString(),
                        dialogViewBinding.postCode.text.toString().toInt(),
                        "İstanbul",
                        dialogViewBinding.streetText.text.toString(),
                        dialogViewBinding.addresDetails.text.toString(),
                        dialogViewBinding.phoneNumberText.text.toString()
                    )
                val result = repository.updateAddress(address)
                result.enqueue(object : Callback<AddressData> {
                    override fun onResponse(
                        call: Call<AddressData>,
                        response: Response<AddressData>
                    ) {
                        println(util.customerId)
                        Toast.makeText(requireContext(), "Güncellendi", Toast.LENGTH_SHORT).show()
                        viewModel.getAddress(address.id)
                        bottomSheetDialog.cancel()
                    }

                    override fun onFailure(call: Call<AddressData>, t: Throwable) {
                        println(t.localizedMessage)
                    }
                })
            } else {
                Toast.makeText(
                    dialogViewBinding.root.context,
                    "Lütfen bütün alanları eksiksiz doldurun...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialogViewBinding.home.setOnClickListener {
            if (homeTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(dialogViewBinding)
            } else {
                name = "Ev"
                jobTick = 0
                homeTick = 1
                partnerTick = 0
                updateIcon(dialogViewBinding)
            }

        }
        dialogViewBinding.job.setOnClickListener {
            if (jobTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(dialogViewBinding)
            } else {
                name = "İş"
                jobTick = 1
                homeTick = 0
                partnerTick = 0
                updateIcon(dialogViewBinding)
            }
        }
        dialogViewBinding.partner.setOnClickListener {
            if (partnerTick == 1) {
                name = ""
                jobTick = 0
                homeTick = 0
                partnerTick = 0
                updateIcon(dialogViewBinding)
            } else {
                name = "Eş"
                jobTick = 0
                homeTick = 0
                partnerTick = 1
                updateIcon(dialogViewBinding)
            }
        }
    }
    fun updateIcon(viewBinding: SelectedAdressViewBinding) {
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
}