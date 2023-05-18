package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentEmailControlBinding
import com.example.sanabi.viewModel.EmailControlViewModel

class EmailControlFragment : Fragment() {
    private lateinit var viewModel: EmailControlViewModel
    private lateinit var binding:FragmentEmailControlBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentEmailControlBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmailControlViewModel::class.java)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.eMail.addTextChangedListener {
            if (it?.isNotEmpty()==true) binding.nextButton.setBackgroundResource(R.drawable.next_button_shape) else binding.nextButton.setBackgroundResource(R.drawable.error_button_shape)
        }
        binding.nextButton.setOnClickListener {
            if (binding.eMail.text.isNotEmpty()){
                observerItem()
                viewModel.getData(requireActivity(),binding.eMail.text.toString())
            }else{
                Toast.makeText(requireContext(), "Lütfen e posta adresinizi giriniz", Toast.LENGTH_SHORT).show()
            }
        }
        binding.nextText.setOnClickListener {
            if (binding.eMail.text.isNotEmpty()){
                observerItem()
                viewModel.getData(requireActivity(),binding.eMail.text.toString())

            }else{
                Toast.makeText(requireContext(), "Lütfen e posta adresinizi giriniz", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backPage.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    fun observerItem(){
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.linearLayout.alpha=0.3F
                binding.progress.visibility=View.VISIBLE
            }else{
                binding.linearLayout.alpha=1F
                binding.progress.visibility=View.INVISIBLE
            }
        }
    }

}