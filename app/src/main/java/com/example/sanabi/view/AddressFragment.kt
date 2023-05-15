package com.example.sanabi.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.ListeningAdressAdapter
import com.example.sanabi.MapsActivity
import com.example.sanabi.databinding.FragmentAddressBinding
import com.example.sanabi.model.AddressData

class AddressFragment : Fragment() {

    private lateinit var viewModel: AddressViewModel
    private lateinit var binding: FragmentAddressBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddressBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        viewModel.addressList.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.errorLinear.visibility=View.VISIBLE
                binding.addressRecycler.visibility=View.INVISIBLE
            }else{
                binding.errorLinear.visibility=View.GONE
                binding.addressRecycler.visibility=View.VISIBLE
                val adapter = ListeningAdressAdapter(it as ArrayList<AddressData>)
                binding.addressRecycler.adapter=adapter
                binding.addressRecycler.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            }
        }
        binding.addAddress.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }
    }
    fun observerItems(){
        viewModel.progress.observe(viewLifecycleOwner){
            if (it) binding.progress.visibility=View.VISIBLE else binding.progress.visibility=View.GONE
        }
    }

    override fun onResume() {
        viewModel.getAdress()
        observerItems()
        super.onResume()
    }
}