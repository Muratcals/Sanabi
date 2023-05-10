package com.example.sanabi.view

import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.sanabi.Adapter.MainCategoryRecycler
import com.example.sanabi.Adapter.MainOfferRecycler
import com.example.sanabi.R
import com.example.sanabi.SearchActivity
import com.example.sanabi.databinding.FragmentProductMainBinding
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.viewModel.ProductMainViewModel

class ProductMainFragment : Fragment() {

    private lateinit var viewModel: ProductMainViewModel
    private lateinit var binding:FragmentProductMainBinding
    private lateinit var adapter :MainCategoryRecycler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProductMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductMainViewModel::class.java)
        viewModel.getCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val incoming =requireActivity().intent.getStringExtra("incoming")!!
        if (incoming.equals("search") || incoming.equals("searchProduct")){
            val id =requireActivity().intent.getIntExtra("categoryId",0)
            val bundle= bundleOf("id" to id)
            findNavController().navigate(R.id.action_productMainFragment_to_categoryContentFragment,bundle)
        }else{
            adapter=MainCategoryRecycler(CategoryModel(arrayListOf(),""))
            binding.categoryRecycler.adapter=adapter
            binding.categoryRecycler.layoutManager=GridLayoutManager(requireContext(),4)
            viewModel.categoryModel.observe(viewLifecycleOwner){
                adapter.updateData(it)
                adapter.notifyDataSetChanged()
            }
            observerItem()
            val offer =ArrayList<String>()
            offer.add("https://images.deliveryhero.io/image/darkstores/Banabi-Campaign-Banners/v3-1904-koltuktacuma_banner.jpg?height=272&dpi=1")
            offer.add("https://images.deliveryhero.io/image/darkstores/Banabi-Campaign-Banners/v2-1904-lipton_bayram.jpg?height=272&dpi=1")
            offer.add("https://images.deliveryhero.io/image/darkstores/Banabi-Campaign-Banners/taze_0704.gif?height=405&dpi=1")
            offer.add("https://images.deliveryhero.io/image/darkstores/Banabi-Campaign-Banners/1804-bayramk_go%CC%88zat.jpg?height=405&dpi=1")
            offer.add("https://images.deliveryhero.io/image/darkstores/Banabi-Campaign-Banners/bayram_barilla_1804.jpg?height=405&dpi=1")
            val adapterOffer =MainOfferRecycler(offer)
            binding.producetOfferRecycler.adapter=adapterOffer
            binding.producetOfferRecycler.apply {
                clipChildren=false
                clipToPadding=false
            }
            binding.mainSearcEditText.setOnClickListener {
                val intent =Intent(requireContext(),SearchActivity::class.java)
                startActivity(intent)
            }
            val compositePageTransformer=CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer((30*(Resources.getSystem().displayMetrics.density)).toInt()))
            binding.producetOfferRecycler.setPageTransformer(compositePageTransformer)
        }
    }

    fun observerItem(){
        viewModel.progress.observe(viewLifecycleOwner){
            if (it) {
                binding.progress.visibility=View.VISIBLE
                binding.productMainScroll.visibility=View.GONE
            }else{
                binding.progress.visibility=View.GONE
                binding.productMainScroll.visibility=View.VISIBLE
            }
        }
    }
}