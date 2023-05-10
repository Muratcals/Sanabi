package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.example.sanabi.model.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sanabi.R
import com.example.sanabi.databinding.FragmentAccountBinding
import com.example.sanabi.viewModel.AccountViewModel

class AccountFragment : Fragment() {

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding:FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        viewModel.getUserInformation()
        observerItem()
        val bundle = Bundle()
        binding.accountName.setOnClickListener {
            bundle.clear()
            bundle.putString("incoming","name")
            findNavController().navigate(R.id.action_accountFragment3_to_accountDetailsFragment,bundle)
        }
        binding.accountPassword.setOnClickListener {
            bundle.clear()
            bundle.putString("incoming","password")
            findNavController().navigate(R.id.action_accountFragment3_to_accountDetailsFragment,bundle)
        }
        binding.accountDt.setOnClickListener {
            bundle.clear()
            bundle.putString("incoming","birthDate")
            findNavController().navigate(R.id.action_accountFragment3_to_accountDetailsFragment,bundle)
        }
        binding.accountPhoneNumber.setOnClickListener {
            bundle.clear()
            bundle.putString("incoming","numberPhone")
            findNavController().navigate(R.id.action_accountFragment3_to_accountDetailsFragment,bundle)
        }
    }

    fun observerItem(){
        viewModel.error.observe(viewLifecycleOwner){

        }
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.progress.visibility=View.VISIBLE
                binding.scrollView3.visibility=View.INVISIBLE
            } else {
                binding.progress.visibility=View.INVISIBLE
                binding.scrollView3.visibility=View.VISIBLE
            }
        }
        viewModel.user.observe(viewLifecycleOwner){user->
            binding.accountNameText.setText("${user.name} ${user.surname}")
            binding.accountDtText.setText(user.birtDate)
            binding.accountPhoneNumberText.setText(user.numberPhone)
            binding.accountMailText.setText(user.mail)
        }
    }

}