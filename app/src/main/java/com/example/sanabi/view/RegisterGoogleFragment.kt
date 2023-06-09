package com.example.sanabi.view

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sanabi.Util.dateFormat
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentRegisterGoogleBinding
import com.example.sanabi.model.Data
import com.example.sanabi.model.UserInformation
import com.example.sanabi.viewModel.RegisterGoogleViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.time.LocalDateTime


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
        viewModel.progress.observe(viewLifecycleOwner){
            if (it) {
                binding.googleProgress.visibility=View.VISIBLE
                binding.scrollView2.alpha=0.3F
                buttonClickable(false)
            }else{
                binding.googleProgress.visibility=View.INVISIBLE
                binding.scrollView2.alpha=1F
                buttonClickable(true)
            }
        }
        binding.googleCalendar.setEndIconOnClickListener {
            val now = LocalDateTime.now()
            val datepickerlistener =
                DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                        cal.set(Calendar.YEAR, p1)
                        cal.set(Calendar.DAY_OF_MONTH, p3)
                        cal.set(Calendar.MONTH, p2)
                       binding.googleCalendarText.dateFormat(cal)
                    }
                }, now.year, now.monthValue - 1, now.dayOfMonth)
            datepickerlistener.show()
        }
        binding.registerButton.setOnClickListener {
            saveData()
            buttonClickable(false)
        }
        binding.registerText.setOnClickListener {
            saveData()
            buttonClickable(false)
        }
    }

    fun saveData() {
        if (!emptyItemControl()) {
            Toast.makeText(
                requireContext(), "Lütfen bütün alanları eksiksiz doldurunuz", Toast.LENGTH_SHORT
            ).show()
            buttonClickable(true)
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
            viewModel.postData(requireActivity() as AppCompatActivity, user.data[0])
        }
    }

    fun buttonClickable(state:Boolean){
        binding.registerButton.isClickable=state
        binding.registerButton.isEnabled=state
        binding.registerText.isEnabled=state
        binding.registerText.isClickable=state
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

    override fun onDestroy() {
        val signIn=GoogleSignIn.getClient(requireActivity(), util.gso)
        signIn.signOut()
        super.onDestroy()
    }
}