package com.example.sanabi.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telecom.Call
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sanabi.API.Repository
import com.example.sanabi.MainActivity
import com.example.sanabi.R
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentSuccessMailBinding
import com.example.sanabi.model.GetIdModel
import com.example.sanabi.viewModel.SuccessMailViewModel
import retrofit2.Callback
import retrofit2.Response

class SuccessMailFragment : Fragment() {

    private lateinit var binding:FragmentSuccessMailBinding
    val repository=Repository()
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
                repository.getIdUser(eMail).enqueue(object :Callback<GetIdModel>{
                    override fun onResponse(
                        call: retrofit2.Call<GetIdModel>,
                        response: Response<GetIdModel>
                    ) {
                        util.customerId=response.body()!!.data
                        val intent =Intent(requireContext(),MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    override fun onFailure(call: retrofit2.Call<GetIdModel>, t: Throwable) {
                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(requireContext(), it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                binding.progress.visibility=View.GONE
                binding.linearLayout.alpha=1F
            }
        }
    }

}