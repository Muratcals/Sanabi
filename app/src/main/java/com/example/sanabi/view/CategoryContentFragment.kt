package com.example.sanabi.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.CategoryContentProductShowRecycler
import com.example.sanabi.Adapter.MainDetailsCategoryNameRecycler
import com.example.sanabi.Room.DatabaseRoom
import com.example.sanabi.Room.RoomServices
import com.example.sanabi.databinding.FragmentCategoryContentBinding
import com.example.sanabi.model.CategoryModel
import com.example.sanabi.model.CategoryModelData
import com.example.sanabi.model.SearchGetCategoryModel
import com.example.sanabi.viewModel.CategoryContentViewModel

class CategoryContentFragment : Fragment() {

    private lateinit var binding: FragmentCategoryContentBinding
    private lateinit var viewModel: CategoryContentViewModel
    private lateinit var categoryNameadapter: MainDetailsCategoryNameRecycler
    private lateinit var productAdapter: CategoryContentProductShowRecycler
    private lateinit var roomDatabase:RoomServices
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryContentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryContentViewModel::class.java)
        val incoming =requireActivity().intent.getStringExtra("incoming")
        if(incoming.equals("searchProduct")){
            val categoryId=requireActivity().intent.getIntExtra("categoryId",0)
            viewModel.getCategory(categoryId)
        }else{
            viewModel.getAllCategorys()
        }
        roomDatabase=DatabaseRoom.getDatabase(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            categoryNameadapter =
                MainDetailsCategoryNameRecycler(arrayListOf(), 1, viewModel)
            binding.categoryNameRecycler.adapter = categoryNameadapter
            observerItem()
            binding.ProductsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.categoryNameRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val categoryId=requireActivity().intent.getIntExtra("categoryId",0)
            val id = it.getInt("id")
            observerAllCategory(id)
            observerSearchCategory(categoryId)
        }
    }

    fun observerSearchCategory(id:Int){
        viewModel.categorySearch.observe(viewLifecycleOwner) {
            viewModel.getAllCategoryProducts(id)
            categoryNameadapter.updateData(arrayListOf(it.data), id)
            binding.categoryNameRecycler.adapter = categoryNameadapter
            observerItem()
        }
    }

    fun observerAllCategory(id:Int){
        viewModel.categorys.observe(viewLifecycleOwner) {
            viewModel.getAllCategoryProducts(id)
            categoryNameadapter.updateData(it.data, id)
            binding.categoryNameRecycler.adapter = categoryNameadapter
            if (id >5){
                binding.categoryNameRecycler.scrollToPosition(id)
            }else{
                binding.categoryNameRecycler.scrollToPosition(id-1)
            }
            observerItem()
        }
    }
    fun observerItem() {
        viewModel.categoryProducts.observe(viewLifecycleOwner) {
            productAdapter = CategoryContentProductShowRecycler(it,viewModel,requireActivity())
            productAdapter.updateData(it)
            binding.ProductsRecycler.adapter = productAdapter
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it) {
                binding.progress.visibility = View.VISIBLE
                binding.ProductsRecycler.visibility = View.GONE
            } else {
                binding.progress.visibility = View.GONE
                binding.ProductsRecycler.visibility = View.VISIBLE
            }
        }
    }

}