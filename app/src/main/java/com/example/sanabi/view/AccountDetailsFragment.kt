package com.example.sanabi.view

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sanabi.R
import com.example.sanabi.Util.dateFormat
import com.example.sanabi.Util.util
import com.example.sanabi.databinding.FragmentAccountDetailsBinding
import com.example.sanabi.model.Data
import com.example.sanabi.viewModel.AccountDetailsViewModel
import java.time.LocalDate

class AccountDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAccountDetailsBinding
    private lateinit var viewModel: AccountDetailsViewModel
    private lateinit var userData: Data
    val cal = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AccountDetailsViewModel::class.java]
        val activity=requireActivity() as AppCompatActivity
        arguments?.let {
            val incoming = it.getString("incoming")
            when (incoming) {
                "name"->{
                    activity.supportActionBar!!.hide()
                    binding.passwordControl.visibility=View.GONE
                }
                "password" -> {
                    binding.headText.setText("Şifre")
                    activity.supportActionBar!!.hide()
                    binding.passwordControl.visibility=View.VISIBLE
                    updatePassword()
                    observerItem()
                }
                "birthDate" -> {
                    binding.headText.setText("Doğum Tarihi")
                    binding.passwordControl.visibility=View.GONE
                    activity.supportActionBar!!.hide()
                    updatedate()
                }
                "numberPhone"->{
                    binding.headText.setText("Telefon Numarası")
                    binding.passwordControl.visibility=View.GONE
                    activity.supportActionBar!!.hide()
                    updatePhone()
                }
            }
            binding.nextButton.setOnClickListener {
                updateData(incoming!!)
            }
            viewModel.user.observe(viewLifecycleOwner) {
                userData = it
                updateUI(incoming!!,it)
            }
            binding.backPage.setOnClickListener {
                activity.onBackPressed()
            }
            binding.nextText.setOnClickListener {
                updateData(incoming!!)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserData()
    }

    fun updateUI(incoming: String, user: Data) {
        when (incoming) {
            "name" -> {
                binding.lastText.setText(user.name)
                binding.secTex.setText(user.surname)
            }
            "birthDate" -> {
                binding.lastText.setText(user.birtDate)
            }
            "numberPhone" -> {
                binding.lastText.setText(user.numberPhone)
            }
        }
    }

    fun updatePassword() {
        binding.informationText.setText("Şifrenizi mi değiştiriyorsunuz? En az 10 karakter kullanın.")
        binding.secTex.visibility=View.GONE
        binding.last.isPasswordVisibilityToggleEnabled = true
        binding.last.setHint("Yeni Şifre")
        binding.lastText.addTextChangedListener {
            viewModel.passwordsize(it.toString())
            viewModel.lowerLetterControl(it.toString())
            viewModel.upperLetterControl(it.toString())
            viewModel.passwordNumberControl(it.toString())
            if (passwordSuccessControl()) {
                binding.nextButton.setBackgroundResource(R.drawable.next_button_shape)
                binding.nextButton.isClickable = true
            } else {
                binding.nextButton.setBackgroundResource(R.drawable.error_button_shape)
                binding.nextButton.isClickable = false
            }
        }
    }

    fun updatePhone() {
        binding.sec.visibility = View.GONE
        binding.lastText.inputType=InputType.TYPE_CLASS_PHONE
        binding.informationText.setText("Telefon numaranızız değiştirirseniz, bir sonraki siparişiniz sırasından sizi bir onay sürecinden geçireceğiz.")
        binding.last.setHint("Telefon No")
    }

    fun updatedate() {
        binding.last.setHint("Doğum Tarihi")
        val local = LocalDate.now()
        binding.sec.visibility = View.GONE
        binding.last.setEndIconDrawable(R.drawable.calendar_icon)
        binding.last.isEndIconVisible = true
        binding.informationText.setText("Lütfen doğum tarihinizi seçiniz.")
        binding.last.setEndIconOnClickListener {
            val datepickerListener = DatePickerDialog(
                requireContext(),
                { datePicker, i, i2, i3 ->
                    cal.set(i, i2, i3)
                    binding.lastText.dateFormat(cal)
                },
                local.year,
                local.month.value,
                local.dayOfMonth
            )
            datepickerListener.show()
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
    }

    fun passwordSuccessControl(): Boolean {
        return (viewModel.passwordSizeControl.value == true && viewModel.lowerLetterControl.value == true && viewModel.upperLetterControl.value == true
                && viewModel.numberControl.value == true && binding.lastText.text!!.isNotEmpty())
    }



    fun updateData(incoming: String) {
        when (incoming) {
            "password" -> {
                viewModel.updatePassword(requireActivity() as AppCompatActivity, binding.lastText.text.toString())
            }
            "name" -> {
                val newData = Data(
                    "",
                    LocalDate.now().toString(),
                    util.customerId,
                    userData.mail,
                    binding.lastText.text.toString(),
                    userData.numberPhone,
                    binding.secTex.text.toString()
                )
                viewModel.updateUser(requireActivity() as AppCompatActivity,newData)
            }
            "birthDate" -> {
                val date =binding.lastText.text.toString()
                val newData = Data(
                    date,
                    "",
                    util.customerId,
                    userData.mail,
                    userData.name,
                    userData.numberPhone,
                    userData.surname
                )
                viewModel.updateUser(requireActivity() as AppCompatActivity,newData)
            }
            "numberPhone" -> {
                val newData = Data(
                    userData.birtDate,
                    "",
                    util.customerId,
                    userData.mail,
                    userData.name,
                    binding.lastText.text.toString(),
                    userData.surname
                )
                viewModel.updateUser(requireActivity() as AppCompatActivity,newData)
            }
        }
    }
}