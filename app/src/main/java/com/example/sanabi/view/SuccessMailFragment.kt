package com.example.sanabi.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sanabi.MainActivity
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentSuccessMailBinding
import com.example.sanabi.viewModel.SuccessMailViewModel

class SuccessMailFragment : Fragment() {

    private lateinit var binding:FragmentSuccessMailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSuccessMailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let{
            val eMail =it.getString("Email")
            binding.eMail.setText(eMail.toString())
            binding.nextText.setOnClickListener {
                loginAuth(eMail.toString(),binding.password.text.toString())
            }
            binding.nextButton.setOnClickListener {
                loginAuth(eMail.toString(),binding.password.text.toString())
            }
        }
    }

    fun loginAuth(eMail:String,password:String){
        binding.progress.visibility=View.VISIBLE
        binding.linearLayout.alpha=0.3F
        util.auth.signInWithEmailAndPassword(eMail,password).addOnCompleteListener {
            if (it.isSuccessful){
                binding.progress.visibility=View.GONE
                binding.linearLayout.alpha=1F
                val intent =Intent(requireContext(),MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }else{
                Toast.makeText(requireContext(), it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                binding.progress.visibility=View.GONE
                binding.linearLayout.alpha=1F
            }
        }
    }

}