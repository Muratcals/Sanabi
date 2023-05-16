package com.example.sanabi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val coming = intent.getStringExtra("incoming")
        if (coming.equals("Google")) {
            val account = intent.getBundleExtra("account")!!.get("account")
            val bundle = bundleOf("account" to account)
            findNavController(R.id.fragmentContainerView).navigate(
                R.id.action_emailControlFragment_to_registerGoogleFragment,
                bundle
            )
        }
    }
    override fun onBackPressed() {
        if (intent.getStringExtra("incoming").equals("Google")) {
            this.finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (intent.getStringExtra("incoming").equals("Google")) {
            val googleSignIn = GoogleSignIn.getClient(applicationContext, util.gso)
            googleSignIn.signOut()
        }
        super.onDestroy()
    }
}