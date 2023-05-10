package com.example.sanabi.view

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.sanabi.MainActivity
import com.example.sanabi.R
import com.example.sanabi.databinding.FragmentRegisterBinding
import com.example.sanabi.model.Data
import com.example.sanabi.viewModel.RegisterViewModel
import com.google.type.DateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var eMail: String
    private lateinit var cal: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        cal = Calendar.getInstance()
        arguments?.let {
            eMail = it.getString("Email")!!
            binding.information.setText("Şimdi ${eMail} ile bir Sanabi hesabı oluşturalım")
        }
        binding.passwordText.addTextChangedListener {
            viewModel.passwordsize(it.toString())
            viewModel.lowerLetterControl(it.toString())
            viewModel.upperLetterControl(it.toString())
            viewModel.passwordNumberControl(it.toString())
            if (passwordSuccessControl()) {
                binding.registerButton.setBackgroundResource(R.drawable.next_button_shape)
                binding.registerButton.isClickable = true
            } else {
                binding.registerButton.setBackgroundResource(R.drawable.error_button_shape)
                binding.registerButton.isClickable = false
            }
        }
        observerItem()
        binding.calendar.setEndIconOnClickListener {
            val now = LocalDateTime.now()
            val datepickerlistener = DatePickerDialog(
                requireContext(),
                { p0, p1, p2, p3 ->
                    cal.set(Calendar.YEAR, p1)
                    cal.set(Calendar.DAY_OF_MONTH-1, p3)
                    cal.set(Calendar.MONTH, p2)
                    dateFormat()
                }, now.year, now.monthValue - 1, now.dayOfMonth
            )
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
                requireContext(),
                "Lütfen bütün alanları eksiksiz doldurunuz.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
                val data =Data(
                    binding.calendarText.text.toString(),
                    "",
                    0,
                    eMail,
                    binding.nameText.text.toString(),
                    binding.phoneText.text.toString(),
                    binding.lastNameText.text.toString()
                )
            val activity =requireActivity() as AppCompatActivity
            viewModel.postData(activity,data,binding.passwordText.text.toString())
        }
    }

    fun observerItem() {
        viewModel.passwordSizeControl.observe(viewLifecycleOwner) {
            if (it) binding.error1.setImageResource(R.drawable.sucess) else binding.error1.setImageResource(
                R.drawable.error
            )
        }
        viewModel.upperLetterControl.observe(viewLifecycleOwner) {
            if (it) binding.error2.setImageResource(R.drawable.sucess) else binding.error2.setImageResource(
                R.drawable.error
            )
        }
        viewModel.lowerLetterControl.observe(viewLifecycleOwner) {
            if (it) binding.error3.setImageResource(R.drawable.sucess) else binding.error3.setImageResource(
                R.drawable.error
            )
        }
        viewModel.numberControl.observe(viewLifecycleOwner) {
            if (it) binding.error4.setImageResource(R.drawable.sucess) else binding.error4.setImageResource(
                R.drawable.error
            )
        }
        viewModel.saveControl.observe(viewLifecycleOwner) {
            if (it){

            }
        }
    }
    fun passwordSuccessControl(): Boolean {
        return (viewModel.passwordSizeControl.value == true && viewModel.lowerLetterControl.value == true && viewModel.upperLetterControl.value == true
                && viewModel.numberControl.value == true && binding.passwordText.text!!.isNotEmpty())
    }

    fun emptyItemControl(): Boolean {
        val control = binding.lastNameText.text!!.isNotEmpty() && binding.calendarText.text!!.isNotEmpty()
                && binding.phoneText.text!!.isNotEmpty() && binding.nameText.text!!.isNotEmpty()
        if (control){
            return if (binding.phoneText.text!!.length!=11){
                Toast.makeText(requireContext(), "Lütfen telefon numaranızı 11 hane olarak giriniz!", Toast.LENGTH_SHORT).show()
                false
            }else{
                true
            }
        }else{
            return false
        }
    }

    fun dateFormat() {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val strDate: String = format.format(cal.getTime())
        binding.calendarText.setText(strDate)
    }
}