package com.example.sanabi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.updatePadding
import com.example.sanabi.API.Repository
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.ActivityMapsBinding
import com.example.sanabi.databinding.FragmentSelectedAdressViewBinding
import com.example.sanabi.databinding.SelectedAdressViewBinding
import com.example.sanabi.model.AddressData
import com.example.sanabi.model.GetIdModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private var marker: Marker? = null
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val repository = Repository()
    var homeTick = 0
    var partnerTick = 0
    var jobTick = 0
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.adressSave.setOnClickListener {
            buttonClickable(false)
            if (marker != null) {
                getAddress(marker!!.position)
            } else {
                val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                getAddress(latLng)
            }
        }
    }

    fun getAddress(latLng: LatLng) {
        val geocoder = Geocoder(this)
        try {
            val adress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
            if (adress!!.isNotEmpty()) {
                textAdress(adress[0])
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
    }

    fun getLocation() {
        buttonClickable(false)
        binding.mapsProgress.visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        } else {
            locationListener = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

                }

                override fun onProviderEnabled(provider: String) {

                }

                override fun onProviderDisabled(provider: String) {
                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(p0: Long) {
                            Toast.makeText(
                                applicationContext,
                                "Lütfen konumuzunu açınız",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        override fun onFinish() {
                            this@MapsActivity.finish()
                            locationManager.removeUpdates(locationListener)
                        }
                    }.start()
                }
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1f,
                locationListener
            )
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.result != null) {
                    binding.mapsProgress.visibility = View.GONE
                    lastLocation = it.result
                    val latLng = LatLng(it.result.latitude, it.result.longitude)
                    markerOnMap(latLng)
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun buttonClickable(state:Boolean){
        binding.adressSave.isEnabled=state
        binding.adressSave.isClickable=state
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                marker = p0
                markerOnMap(p0.position)
            }

            override fun onMarkerDragStart(p0: Marker) {

            }
        })
    }

    fun textAdress(adress: Address) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val dialogViewBinding = SelectedAdressViewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogViewBinding.root)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.show()
        dialogViewBinding.lastText.setText("${adress.subLocality} ${adress.thoroughfare} ${adress.subThoroughfare}")
        dialogViewBinding.secText.setText("${adress.subAdminArea} ${adress.locality ?: "İstanbul"} ${adress.postalCode}")
        dialogViewBinding.postCode.setText(adress.postalCode)
        dialogViewBinding.neighbourhoodText.setText(adress.subLocality)
        dialogViewBinding.districteText.setText(adress.subAdminArea)
        dialogViewBinding.buildingNoText.setText(adress.subThoroughfare)
        dialogViewBinding.streetText.setText(adress.thoroughfare)
        dialogViewBinding.selectedAddressCancel.setOnClickListener {
            bottomSheetDialog.setCancelable(true)
            buttonClickable(true)
            bottomSheetDialog.cancel()
        }
        dialogViewBinding.save.setOnClickListener {
            dialogViewBinding.save.isEnabled = false
            dialogViewBinding.save.isClickable = false
            if (dialogViewBinding.postCode.text!!.isNotEmpty() && dialogViewBinding.apartmentNumberText.text!!.isNotEmpty()
                && dialogViewBinding.districteText.text!!.isNotEmpty() && dialogViewBinding.buildingNoText.text!!.isNotEmpty()
                && dialogViewBinding.neighbourhoodText.text!!.isNotEmpty() && dialogViewBinding.streetText.text!!.isNotEmpty() &&
                dialogViewBinding.phoneNumberText.text!!.isNotEmpty() && dialogViewBinding.floorText.text!!.isNotEmpty()
            ) {
                val result = repository.getIdUser(util.auth.currentUser!!.email.toString())
                result.enqueue(object : Callback<GetIdModel> {
                    override fun onResponse(
                        call: Call<GetIdModel>,
                        response: Response<GetIdModel>
                    ) {
                        val address = AddressData(
                            dialogViewBinding.apartmentNumberText.text.toString().toInt(),
                            dialogViewBinding.buildingNoText.text.toString().toInt(),
                            "${
                                LocalDate.now()
                            } ${LocalTime.now().hour}:${LocalTime.now().minute}",
                            response.body()!!.data,
                            dialogViewBinding.districteText.text.toString(),
                            0,
                            name,
                            dialogViewBinding.neighbourhoodText.text.toString(),
                            dialogViewBinding.postCode.text.toString().toInt(),
                            adress.locality ?: "İstanbul",
                            dialogViewBinding.streetText.text.toString(),
                            dialogViewBinding.addresDetails.text.toString(),
                            dialogViewBinding.phoneNumberText.text.toString()
                        )
                        saveData(bottomSheetDialog, address, dialogViewBinding)
                    }

                    override fun onFailure(call: Call<GetIdModel>, t: Throwable) {
                        dialogViewBinding.save.isEnabled = true
                        dialogViewBinding.save.isClickable = true
                        Toast.makeText(this@MapsActivity, t.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            } else {
                Toast.makeText(
                    dialogViewBinding.root.context,
                    "Lütfen bütün alanları eksiksiz doldurun...",
                    Toast.LENGTH_SHORT
                ).show()
                dialogViewBinding.save.isEnabled = true
                dialogViewBinding.save.isClickable = true
            }
        }
        dialogViewBinding.home.setOnClickListener {
            name = "Ev"
            jobTick = 0
            homeTick = 1
            partnerTick = 0
            updateIcon(dialogViewBinding)
        }
        dialogViewBinding.job.setOnClickListener {
            name = "İş"
            jobTick = 1
            homeTick = 0
            partnerTick = 0
            updateIcon(dialogViewBinding)
        }
        dialogViewBinding.partner.setOnClickListener {
            name = "Eş"
            jobTick = 0
            homeTick = 0
            partnerTick = 1
            updateIcon(dialogViewBinding)
        }
    }

    fun updateIcon(viewBinding: SelectedAdressViewBinding) {
        if (homeTick == 1) {
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home)
            viewBinding.homeIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.homeIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.homeIcon.setImageResource(R.drawable.home_icon)
            viewBinding.homeIcon.updatePadding(30, 30, 30, 30)
        }
        if (jobTick == 1) {
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work)
            viewBinding.jobIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.jobIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.jobIcon.setImageResource(R.drawable.work_icon)
            viewBinding.jobIcon.updatePadding(30, 30, 30, 30)
        }
        if (partnerTick == 1) {
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_selected_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart)
            viewBinding.partnerIcon.updatePadding(30, 30, 30, 30)
        } else {
            viewBinding.partnerIcon.setBackgroundResource(R.drawable.circle_shape)
            viewBinding.partnerIcon.setImageResource(R.drawable.heart_icon)
            viewBinding.partnerIcon.updatePadding(30, 30, 30, 30)
        }
    }

    private fun markerOnMap(currentLatLong: LatLng) {
        buttonClickable(true)
        mMap.clear()
        val markerOptions = MarkerOptions().position(currentLatLong).draggable(true)
        markerOptions.title("$currentLatLong")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18F))
        mMap.addMarker(markerOptions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Lütfen konum bilgilerine izin verin", Toast.LENGTH_SHORT)
                        .show()
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 1
                    )
                    this.finish()
                } else {
                    getLocation()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun saveData(
        dialog: BottomSheetDialog, item: AddressData,
        view: SelectedAdressViewBinding
    ) {
        val result = repository.postAddress(item)
        result.enqueue(object : Callback<AddressData> {
            override fun onResponse(call: Call<AddressData>, response: Response<AddressData>) {
                dialog.cancel()
                Toast.makeText(this@MapsActivity, "Adres Eklendi", Toast.LENGTH_SHORT).show()
                this@MapsActivity.finish()
                locationManager.removeUpdates(locationListener)
            }

            override fun onFailure(call: Call<AddressData>, t: Throwable) {
                println(t.localizedMessage)
                view.save.isEnabled = true
                view.save.isClickable = true
            }
        })
    }

}