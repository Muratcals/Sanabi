package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.LastOrderProductsRecycler
import com.example.sanabi.Adapter.LastOrdersRecycler
import com.example.sanabi.R
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LastOrderViewModel::class.java)
        viewModel.getCustomerOrders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customerOrderList.observe(viewLifecycleOwner){
            adapter= LastOrdersRecycler(it)
            binding.lastOrderRecycler.adapter=adapter
            binding.lastOrderRecycler.layoutManager=LinearLayoutManager(requireContext())
        }
    }

}