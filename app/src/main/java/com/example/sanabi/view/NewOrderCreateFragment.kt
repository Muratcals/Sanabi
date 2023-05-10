package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.sanabi.Adapter.OrderContentProductsRecycler
import com.example.sanabi.Adapter.OrderSaysProductRecycler
import com.example.sanabi.R
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.databinding.FragmentNewOrderCreateBinding
import com.example.sanabi.model.BasketProductModelData
import com.example.sanabi.viewModel.NewOrderCreateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class NewOrderCreateFragment : Fragment() {

    private lateinit var viewModel: NewOrderCreateViewModel
    private lateinit var binding: FragmentNewOrderCreateBinding
    var sumPrices = 0.0
    var subtotal = 0.0
    val transferPrice = 16.25
    private lateinit var productAdapter: OrderContentProductsRecycler

    private val saysProductAdapter by lazy {
        OrderSaysProductRecycler(arrayListOf(), viewModel)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewOrderCreateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewOrderCreateViewModel::class.java)
        viewModel.getSaysProduct(requireView())
        productAdapter = OrderContentProductsRecycler(arrayListOf(), viewModel)
        //val animation =(requireActivity() as AppCompatActivity).findViewById<LottieAnimationView>(R.id.animation)
        binding.orderProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.saysProductRecycler.adapter = saysProductAdapter
        observerItem()
        binding.saysProductRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.getBasket(requireView()).onEach{ list ->
            sumPrices = 0.0
            productAdapter.updateData(list)
            binding.orderProductRecycler.adapter = productAdapter
            for (onlyBasket in list) {
                sumPrices += (onlyBasket.price * onlyBasket.amount)
            }
            binding.orderSumPrice.setText(viewModel.decimalFormet(sumPrices))
            binding.sumPrice.setText(viewModel.decimalFormet(sumPrices))
            subtotal = sumPrices + transferPrice
            binding.orderSumPrice.setText("Toplam Tutar :${viewModel.decimalFormet(subtotal)}")
            orderCompleteControl()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.saysItems.observe(viewLifecycleOwner) {
            saysProductAdapter.updateData(it)
        }
        binding.orderNextButton.setOnClickListener {
            val bundle = bundleOf("productsPrice" to sumPrices)
            findNavController().navigate(
                R.id.action_newOrderCreateFragment_to_orderPaymentFragment,
                bundle
            )
        }
    }
    fun observerItem() {
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it) {
                binding.scrollView4.visibility=View.GONE
                binding.orderCreateProgress.visibility = View.VISIBLE
            } else {
                binding.scrollView4.visibility=View.VISIBLE
                binding.orderCreateProgress.visibility = View.GONE
            }
        }
    }

    fun orderCompleteControl() {
        if (sumPrices < 100) {
            binding.information.visibility = View.VISIBLE
            binding.orderNextButton.setBackgroundResource(R.drawable.error_button_shape)
            binding.information.setText(
                "Siparişinizi tamamlamak için sepetinize ${
                    viewModel.decimalFormet(
                        100 - sumPrices
                    )
                } TL daha ürün eklemelisiniz"
            )
            binding.orderNextButton.isClickable = false
            binding.orderNextButton.isEnabled = false
        } else {
            binding.orderNextButton.setBackgroundResource(R.drawable.next_button_shape)
            binding.information.visibility = View.GONE
            binding.orderNextButton.isClickable = true
            binding.orderNextButton.isEnabled = true
        }
    }
}