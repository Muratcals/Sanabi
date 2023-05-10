package com.example.sanabi

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.DialogCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.API.Repository
import com.example.sanabi.Adapter.MainAddressControlRecycler
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.ActivityMainBinding
import com.example.sanabi.databinding.MenuHeaderBinding
import com.example.sanabi.databinding.SelectedAdressDialogRecyclerBinding
import com.example.sanabi.databinding.SelectedAdressRecyclerViewBinding
import com.example.sanabi.model.AddressModel
import com.example.sanabi.model.GetAddressModel
import com.example.sanabi.model.GetIdModel
import com.example.sanabi.model.GetUserInformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val repository = Repository()
    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var menuHeader: MenuHeaderBinding
    private lateinit var addressAdapter: MainAddressControlRecycler
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        getFindById()
        menuHeader = MenuHeaderBinding.inflate(layoutInflater)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toogle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        toogle.isDrawerSlideAnimationEnabled = true
        toogle.syncState();
        binding.navigationView.addHeaderView(menuHeader.root)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            val intent = Intent(applicationContext, MenuActivity::class.java)
            when (menuItem.getItemId()) {
                R.id.accountFragment -> {
                    intent.putExtra("incoming", "account")
                    startActivity(intent)
                    true
                }
                R.id.lastorder -> {
                    intent.putExtra("incoming", "order")
                    startActivity(intent)
                    true
                }
                R.id.selectedAdress -> {
                    intent.putExtra("incoming", "address")
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.market.setOnClickListener {
            val intent = Intent(applicationContext, MainDetailsActivity::class.java)
            intent.putExtra("incoming", "main")
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    fun getFindById() {
        binding.mainLayout.alpha=0.3f
        binding.market.isEnabled=false
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        binding.mainProgress.visibility=View.VISIBLE
        repository.getIdUser(util.auth.currentUser!!.email.toString())
            .enqueue(object : Callback<GetIdModel> {
                override fun onResponse(call: Call<GetIdModel>, response: Response<GetIdModel>) {
                    if (response.body() != null) {
                        util.customerId = response.body()!!.data
                        repository.getIdUserData(response.body()!!.data)
                            .enqueue(object : Callback<GetUserInformation> {
                                override fun onResponse(
                                    call: Call<GetUserInformation>,
                                    responses: Response<GetUserInformation>
                                ) {
                                    menuHeader.adLastName.setText("${responses.body()!!.data.name} ${responses.body()!!.data.surname}")
                                    selectedAdressDialog()
                                }

                                override fun onFailure(
                                    call: Call<GetUserInformation>,
                                    t: Throwable
                                ) {
                                    call.request()
                                }
                            })
                    }
                }
                override fun onFailure(call: Call<GetIdModel>, t: Throwable) {
                    println("${t.localizedMessage} tamma ")
                }
            })
    }

    fun selectedAdressDialog() {
        binding.mainLayout.alpha=1f
        binding.market.isEnabled=true
        binding.drawerLayout.isEnabled=true
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.mainProgress.visibility=View.INVISIBLE
        val dialog = BottomSheetDialog(this)
        val view = SelectedAdressDialogRecyclerBinding.inflate(layoutInflater)
        dialog.setCancelable(false)
        dialog.show()
        dialog.setContentView(view.root)
        val addressList = repository.getSelectedAddress(util.customerId)
        addressAdapter = MainAddressControlRecycler(AddressModel(arrayListOf(), ""))
        addressList.enqueue(object : Callback<AddressModel> {
            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {
                if (response.isSuccessful) {
                    addressAdapter.updateData(response.body()!!)
                    view.customerAddressRecycler.adapter = addressAdapter
                    view.customerAddressRecycler.layoutManager =
                        LinearLayoutManager(applicationContext)
                }
            }

            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
        view.successLocation.setOnClickListener {
            sharedPreferences = getSharedPreferences("AddressId", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (addressAdapter.selectedAdress == 0) {
                Toast.makeText(applicationContext, "Adres seçiniz", Toast.LENGTH_SHORT).show()
            } else {
                editor.putInt("addressId", addressAdapter.selectedAdress)
                toolbarAddresUpdate()
                editor.apply()
                dialog.cancel()
            }
            println(sharedPreferences.getInt("addressId", 0))
        }
    }

    fun toolbarAddresUpdate(){
        sharedPreferences = getSharedPreferences("AddressId", Context.MODE_PRIVATE)
        val id =sharedPreferences.getInt("addressId", 0)
        repository.getAddressInformation(id).enqueue(object:Callback<GetAddressModel>{
            override fun onResponse(call: Call<GetAddressModel>, response: Response<GetAddressModel>) {
                binding.toolbar.openAdress.setText("${response.body()!!.data.neighbourhood} Mah. ${response.body()!!.data.street} No:${response.body()!!.data.buildingNo}")
                binding.toolbar.openAdressDetails.setText("${response.body()!!.data.districte} ${response.body()!!.data.province} ")
                println(response.body()!!.data.neighbourhood)
            }
            override fun onFailure(call: Call<GetAddressModel>, t: Throwable) {
                println(t.localizedMessage)
            }

        })
    }
}