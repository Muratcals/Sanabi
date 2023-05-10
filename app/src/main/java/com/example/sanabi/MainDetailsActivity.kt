package com.example.sanabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.room.RoomDatabase
import androidx.window.layout.WindowMetricsCalculator
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.databinding.ActivityMainDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainDetailsActivity : AppCompatActivity() {
    enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

    private lateinit var binding:ActivityMainDetailsBinding
    var incoming =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        binding.toolbar.backpage.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.basketIcon.setOnClickListener {
            val intent = Intent(applicationContext,OrderActivity::class.java)
            startActivity(intent)
        }
    }
    fun dene (){
        val metrics =WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
        val widhtDp =metrics.bounds.width()/resources.displayMetrics.density
        val widhtDpWindowSizeClass=when{
            widhtDp<600f->WindowSizeClass.COMPACT
            widhtDp<840f->WindowSizeClass.MEDIUM
            else -> {
                WindowSizeClass.EXPANDED
            }
        }
        val heightDp = metrics.bounds.height() /
                resources.displayMetrics.density
        val heightWindowSizeClass = when {
            heightDp < 480f -> WindowSizeClass.COMPACT
            heightDp < 900f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
    }

    override fun onBackPressed() {
        if (intent.getStringExtra("incoming").equals("search") ||intent.getStringExtra("incoming").equals("searchProduct") ){
            this.finish()
        }else{
            super.onBackPressed()
        }
    }
}