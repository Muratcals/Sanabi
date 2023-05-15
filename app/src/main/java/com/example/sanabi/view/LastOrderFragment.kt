package com.example.sanabi.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.LastOrderProductsRecycler
import com.example.sanabi.Adapter.LastOrdersRecycler
import com.example.sanabi.LastOrderModel.OrderProductModel
import com.example.sanabi.MainDetailsActivity
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentLastOrderBinding
import com.example.sanabi.databinding.LastOrderViewBinding
import com.example.sanabi.viewModel.LastOrderViewModel

class LastOrderFragment : Fragment() {

    private lateinit var viewModel: LastOrderViewModel
    private lateinit var binding:FragmentLastOrderBinding
    private lateinit var adapter:LastOrdersRecycler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentLastOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LastOrderViewModel::class.java)
        viewModel.getCustomerOrders(util.customerId)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customerOrderList.observe(viewLifecycleOwner){
            if (it.size<=0) {
                binding.emptyOrderLayout.visibility=View.VISIBLE
                binding.lastOrderRecycler.visibility=View.GONE
            }else{
                binding.emptyOrderLayout.visibility=View.GONE
                binding.lastOrderRecycler.visibility=View.VISIBLE
                adapter= LastOrdersRecycler(it,viewModel,requireActivity())
                binding.lastOrderRecycler.adapter=adapter
                binding.lastOrderRecycler.layoutManager=LinearLayoutManager(requireContext())
            }
        }
        binding.emptyOrderDiscover.setOnClickListener {
            val intent =Intent(requireContext(),MainDetailsActivity::class.java)
            intent.putExtra("incoming","lastOrder")
            startActivity(intent)
        }
    }
}