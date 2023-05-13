package com.example.sanabi

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.myBroadcastReceiver
import com.example.sanabi.databinding.ActivityOrderBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OrderActivity : AppCompatActivity(),myBroadcastReceiver.ConnectivityReceiverListener  {
     lateinit var binding:ActivityOrderBinding
    private val myBroadcastReceiver = myBroadcastReceiver()
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

    override fun onResume() {
        super.onResume()
        myBroadcastReceiver.internetConnectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        println(isConnected)
        showMessage(isConnected)
    }

    fun showMessage(isConnected: Boolean){
        val dialogBuilder =AlertDialog.Builder(applicationContext)
        dialogBuilder.setTitle("Internet bağlantısı yok")
        val dialog =dialogBuilder.create()
        dialogBuilder.setPositiveButton("Tekrar dene",DialogInterface.OnClickListener { dialogInterface, i ->
            dialog.cancel()
        })
        dialogBuilder.setNegativeButton("Çıkış",DialogInterface.OnClickListener { dialogInterface, i ->
            application.onTerminate()
        })
        dialog.show()
    }
}