package com.example.sanabi.view

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sanabi.databinding.FragmentRegisterGoogleBinding
import com.example.sanabi.model.Data
import com.example.sanabi.model.UserInformation
import com.example.sanabi.viewModel.RegisterGoogleViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.time.LocalDateTime

import java.util.*

class RegisterGoogleFragment : Fragment() {


    private lateinit var binding: FragmentRegisterGoogleBinding
    private lateinit var viewModel: RegisterGoogleViewModel
    private lateinit var cal: Calendar
    private lateinit var eMail: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterGoogleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterGoogleViewModel::class.java)
        cal = Calendar.getInstance()
        arguments?.let {
            val account = it.get("account") as GoogleSignInAccount
            eMail = account.email.toString()
            binding.googleNameText.setText(account.displayName)
            binding.googlemailText.setText(account.email)
            binding.googleLastNameText.setText(account.familyName)
        }
        binding.googleCalendar.setEndIconOnClickListener {
            val now = LocalDateTime.now()
            val datepickerlistener =
                DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                        cal.set(Calendar.YEAR, p1)
                        cal.set(Calendar.DAY_OF_MONTH, p3)
                        cal.set(Calendar.MONTH, p2)
                        dateFormat()
                    }
                }, now.year, now.monthValue - 1, now.dayOfMonth)
            datepickerlistener.show()
        }
        binding.registerButton.setOnClickListener {
            saveData()
        }
        binding.registerText.setOnClickListener {
            saveData()
        }
    }

    fun saveData() {
        if (!emptyItemControl()) {
            Toast.makeText(
                requireContext(), "Lütfen bütün alanları eksiksiz doldurunuz", Toast.LENGTH_SHORT
            ).show()
        } else {
            val data = ArrayList<Data>()
            data.add(
                Data(
                    binding.googleCalendarText.text.toString(),
                    "",
                    0,
                    eMail,
                    binding.googleNameText.text.toString(),
                    binding.googlePhoneText.text.toString(),
                    binding.googleLastNameText.text.toString()
                )
            )
            val user = UserInformation(data, null)
            viewModel.postData(binding.googlemailText.text.toString(),requireActivity() as AppCompatActivity, user.data[0])
        }
    }

    fun emptyItemControl(): Boolean {
        val control =
            binding.googlePhoneText.text!!.isNotEmpty() && binding.googleCalendarText.text!!.isNotEmpty() && binding.googleLastNameText.text!!.isNotEmpty() && binding.googleNameText.text!!.isNotEmpty()
        if (control) {
            return if (binding.googlePhoneText.text!!.length != 11) {
                Toast.makeText(
                    requireContext(),
                    "Lütfen telefon numaranızı 11 hane olarak giriniz!",
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        } else {
            return false
        }
    }

    fun dateFormat() {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val strDate: String = format.format(cal.getTime())
        binding.googleCalendarText.setText(strDate)
    }
}