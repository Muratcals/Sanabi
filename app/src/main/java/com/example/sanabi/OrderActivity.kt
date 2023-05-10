package com.example.sanabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.room.Database
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.databinding.ActivityOrderBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OrderActivity : AppCompatActivity() {
     lateinit var binding:ActivityOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderBinding.inflate(layoutInflater)
        setSupportActionBar(binding.mainToolbar.root)
        setContentView(binding.root)
        binding.mainToolbar.toolbarText.setText("Sepetim")
        binding.mainToolbar.quit.setOnClickListener {
            this.finish()
        }
        val database =DatabaseRoom.getDatabase(this).roomDb()
        database.getAllBaskets().onEach {
            if (it.isEmpty()){
                binding.emptyBasketLayout.visibility=View.VISIBLE
                binding.notEmptyBasketLayout.visibility=View.GONE
            }else{
                binding.emptyBasketLayout.visibility=View.GONE
                binding.notEmptyBasketLayout.visibility=View.VISIBLE
            }
        }.launchIn(MainScope())
        binding.discover.setOnClickListener {
            val intent =Intent(this,MainDetailsActivity::class.java)
            intent.putExtra("incoming","main")
            startActivity(intent)
            this.finish()
        }
        //binding.animation.playAnimation()
        object : CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {

            }
            override fun onFinish() {
                //binding.animation.pauseAnimation()
            }
        }.start()
    }
}