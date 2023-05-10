package com.example.sanabi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.sanabi.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val coming =intent.getStringExtra("coming")
        if (coming.equals("Google")){
            val account =intent.getBundleExtra("account")!!.get("account")
            val bundle = bundleOf("account" to account)
            findNavController(R.id.fragmentContainerView).navigate(R.id.action_emailControlFragment_to_registerGoogleFragment,bundle)
        }else{

        }
    }
}