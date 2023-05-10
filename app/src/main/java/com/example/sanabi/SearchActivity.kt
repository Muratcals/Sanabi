package com.example.sanabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sanabi.Adapter.PastSearchRecycler
import com.example.sanabi.Adapter.SearchCategoryRecycler
import com.example.sanabi.Adapter.SearchProductsRecycler
import com.example.sanabi.databinding.ActivitySearchBinding
import com.example.sanabi.model.ProductModel
import com.example.sanabi.model.SearchProductModel
import com.example.sanabi.viewModel.SearchActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    val viewModel = SearchActivityViewModel()
    private lateinit var categoryAdapter: SearchCategoryRecycler
    private lateinit var pasteSearchAdapter: PastSearchRecycler
    private lateinit var searchProduct: ProductModel
    private lateinit var searchProductsRecycler: SearchProductsRecycler
    val searchList = ArrayList<SearchProductModel>()
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getCategory()
        viewModel.getSearchProduct().enqueue(object : Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                searchProduct = response.body()!!
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                println(t.localizedMessage)
            }
        })
        viewModel.categoryList.observe(this) {
            categoryAdapter = SearchCategoryRecycler(it)
            binding.searchCategoryRecycler.adapter = categoryAdapter
            binding.searchCategoryRecycler.layoutManager = GridLayoutManager(applicationContext, 2)
        }
        viewModel.getPasteSearch(applicationContext)
        viewModel.pastSearchList.observe(this) {
            binding.pasteSearchLayout.visibility = View.VISIBLE
            pasteSearchAdapter = PastSearchRecycler(it,this)
            binding.pasteSearchRecycler.adapter = pasteSearchAdapter
            binding.pasteSearchRecycler.layoutManager = GridLayoutManager(applicationContext, 2)
        }
        binding.deletePasteSearchItems.setOnClickListener {
            viewModel.deletePastSearch(applicationContext)
            viewModel.getPasteSearch(it.context)
        }
        binding.searchBackPage.setOnClickListener {
            this.finish()
        }
        binding.searchEditText.addTextChangedListener {
            binding.thisSearch.setText("Bunu ara: ${it}")
            searchList.clear()
            if (it.toString().isNotEmpty()){
                if (searchProduct.data.isNotEmpty()) {
                    for (items in searchProduct.data) {
                        if (items.name.toLowerCase().contains(it.toString().toLowerCase())) {
                            searchList.add(
                                SearchProductModel(
                                    items.categoryId,
                                    items.createdDate,
                                    items.description,
                                    items.id,
                                    items.image,
                                    items.name,
                                    items.price,
                                    items.stock
                                )
                            )
                        }
                        binding.searchDetailsLayout.visibility = View.GONE
                        binding.searcTextLayout.visibility = View.VISIBLE
                        searchProductsRecycler = SearchProductsRecycler(searchList, this)
                        binding.searchEditTextRecycler.adapter = searchProductsRecycler
                        binding.searchEditTextRecycler.layoutManager =
                            LinearLayoutManager(applicationContext)
                    }
                } else {
                    binding.searchDetailsLayout.visibility = View.VISIBLE
                    binding.searcTextLayout.visibility = View.GONE
                }
            }else{
                binding.searchDetailsLayout.visibility = View.VISIBLE
                binding.searcTextLayout.visibility = View.GONE
            }
        }
    }

    fun searchTextUpdate(string: String) {
        binding.searchEditText.setText(string)
    }
    fun postSearchProductCategory(id:Int){
        val intent = Intent(applicationContext,MainDetailsActivity::class.java)
        intent.putExtra("incoming","searchProduct")
        intent.putExtra("categoryId",id)
        startActivity(intent)
    }
}