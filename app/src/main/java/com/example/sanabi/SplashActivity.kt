package com.example.sanabi

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.sanabi.API.Repository
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.ActivitySplashBinding
import com.example.sanabi.databinding.LoginAlertBinding
import com.example.sanabi.model.GetIdModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {

    private lateinit var googleSignIn:GoogleSignInClient
    private lateinit var binding :ActivitySplashBinding
    private val repository=Repository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSignIn=GoogleSignIn.getClient(applicationContext,util.gso)
        val incoming =intent.getStringExtra("incoming")
        if (incoming==null){
            val textAnim=AnimationUtils.loadAnimation(applicationContext,R.anim.splas_animation)
            binding.splashText.startAnimation(textAnim)
            object:CountDownTimer(4000,1000){
                override fun onTick(p0: Long) {
                    if (p0.toInt()<2000 && binding.splashText.isVisible){
                        binding.splashText.visibility=View.INVISIBLE
                        binding.splashAnim.visibility=View.VISIBLE
                        binding.splashAnim.playAnimation()
                    }
                }
                override fun onFinish() {
                    if (util.internetControl(this@SplashActivity)){
                        loginAlertDialog()
                    }else{
                        Toast.makeText(applicationContext, "Internet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }else{
            if (util.internetControl(this)){
                loginAlertDialog()
            }else{
                Toast.makeText(applicationContext, "Internet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun loginAlertDialog() {
        if (util.auth.currentUser!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }else{
            val bottomSheetDialog = BottomSheetDialog(this)
            val binding: LoginAlertBinding = LoginAlertBinding.inflate(layoutInflater)
            bottomSheetDialog.setContentView(binding.root)
            bottomSheetDialog.show()
            binding.signInMail.setOnClickListener{
                val intent=Intent(this,LoginActivity::class.java)
                intent.putExtra("incoming","account")
                startActivity(intent)
            }
            binding.signInGoogle.setOnClickListener{
                val intent =googleSignIn.signInIntent
                startActivityForResult(intent,100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==100){
            if (data!=null){
                val task =GoogleSignIn.getSignedInAccountFromIntent(data)
                if (task.isSuccessful){
                    val account =task.result
                    authInformation(account)
                }
            }
        }
    }

    fun authInformation(account:GoogleSignInAccount){
        val intent =Intent(this,LoginActivity::class.java)
        util.auth.fetchSignInMethodsForEmail(account.email.toString()).addOnSuccessListener {
            if (it.signInMethods?.isNotEmpty()==true && it!=null){
                if (it.signInMethods!![0].toString().equals("password")){
                    Toast.makeText(applicationContext, "Bu mail adresi şifre ile giriş yapmıştır. Şifre ile giriş yapmayı deneyiniz.", Toast.LENGTH_SHORT).show()
                    googleSignIn.signOut()
                }else{
                    repository.getIdUser(account.email.toString()).enqueue(object :Callback<GetIdModel>{
                        override fun onResponse(
                            call: Call<GetIdModel>,
                            response: Response<GetIdModel>
                        ) {
                            util.customerId=response.body()!!.data
                            val credential =GoogleAuthProvider.getCredential(account.idToken,null)
                            util.auth.signInWithCredential(credential).addOnSuccessListener {
                                val intents =Intent(applicationContext,MainActivity::class.java)
                                startActivity(intents)
                                this@SplashActivity.finish()
                            }
                        }

                        override fun onFailure(call: Call<GetIdModel>, t: Throwable) {
                            Toast.makeText(applicationContext, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }else{
                val bundle = bundleOf("account" to account)
                intent.putExtra("account",bundle)
                intent.putExtra("incoming","Google")
                startActivity(intent)
            }
        }
    }
}