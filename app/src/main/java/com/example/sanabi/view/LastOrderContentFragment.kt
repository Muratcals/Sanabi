package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.LastOrderProductsRecycler
import com.example.sanabi.R
import com.example.sanabi.Util.decimalFormet
import com.example.sanabi.databinding.FragmentLastOrderContentBinding
import com.example.sanabi.viewModel.LastOrderContentViewModel

class LastOrderContentFragment : Fragment() {

    private lateinit var binding:FragmentLastOrderContentBinding
    private lateinit var viewModel: LastOrderContentViewModel
    private lateinit var orderProductContentAdapter:LastOrderProductsRecycler
    val transferPrice=15.25
    var sumPrice=0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentLastOrderContentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       arguments?.let {
           val id =it.getInt("orderId")
           viewModel = ViewModelProvider(this).get(LastOrderContentViewModel::class.java)
           viewModel.getLastOrder(id)
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.orderContent.observe(viewLifecycleOwner){
            sumPrice=0.0
            orderProductContentAdapter= LastOrderProductsRecycler(it[0]._Products,viewModel)
            binding.orderContentRecycler.adapter=orderProductContentAdapter
            binding.orderContentRecycler.layoutManager=LinearLayoutManager(requireContext())
            for (items in it[0]._Products){
               sumPrice+=(items.productQuantity*items.product.price)
            }
            binding.lastOrderProductPrice.decimalFormet(sumPrice)
            if (it[0].paymentTypeId==1){
                binding.paymentMethodImage.setImageResource(R.drawable.card_black_icon)
            }else{
                binding.paymentMethodImage.setImageResource(R.drawable.purse_black_icon)
            }
            binding.paymentMethodPrice.decimalFormet(sumPrice+transferPrice)
            binding.paymentMethodName.setText(it[0].paymentType.typeName)
            binding.lastOrderSumprice.decimalFormet((sumPrice+transferPrice))
        }
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.lastOrderScrollView.visibility=View.GONE
                binding.lastOrderProgress.visibility=View.VISIBLE
            }else{
                binding.lastOrderScrollView.visibility=View.VISIBLE
                binding.lastOrderProgress.visibility=View.GONE
            }
        }
        viewModel.addressInformation.observe(viewLifecycleOwner){
            binding.lastOrderAddressText.setText("${it.data.neighbourhood} Mah. ${it.data.street} No: ${it.data.buildingNo} ${it.data.districte} ${it.data.province}")
        }
    }
}