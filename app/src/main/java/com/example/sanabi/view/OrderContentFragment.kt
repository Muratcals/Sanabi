package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sanabi.R
import com.example.sanabi.viewModel.OrderContentViewModel

class OrderContentFragment : Fragment() {

    companion object {
        fun newInstance() = OrderContentFragment()
    }

    private lateinit var viewModel: OrderContentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderContentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}