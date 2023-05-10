package com.example.sanabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.sanabi.databinding.ActivityMenuBinding
import com.example.sanabi.view.AddressFragment

class MenuActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.menuToolbar.root)
        val incoming =intent.getStringExtra("incoming")
        if (incoming.equals("account")) {
            binding.menuToolbar.toolbarText.setText("Hesabım")
            findNavController(R.id.fragmentContainerView2).setGraph(R.navigation.account_nav_grapht)
        }
        else if (incoming.equals("order")){
            binding.menuToolbar.toolbarText.setText("Önceki siparişlerim")
        }
        else if (incoming.equals("address")){
            binding.menuToolbar.toolbarText.setText("Adreslerim")
            supportFragmentManager.commit {
                this.replace(R.id.fragmentContainerView2,AddressFragment())
            }
        }
        binding.menuToolbar.quit.setOnClickListener {
            onBackPressed()
        }
    }
}