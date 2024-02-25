package com.pro.easyfood.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pro.easyfood.R
import com.pro.easyfood.activities.MainActivity
import com.pro.easyfood.activities.MealActivity
import com.pro.easyfood.adapters.FavoriteMealsAdapter
import com.pro.easyfood.databinding.FragmentSearchBinding
import com.pro.easyfood.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var searchRecyclerViewAdapter: FavoriteMealsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        binding.imgSearch.setOnClickListener {
            searchMeals()
        }

        observeSearchMealsLiveData()
        onItemClick()

        var searchJob: Job? = null
        binding.edSearchBox.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                viewModel.searchMeal(it.toString())
                delay(500)
            }
        }
    }

    private fun onItemClick() {
        searchRecyclerViewAdapter.onCLick = {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, it.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, it.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, it.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observeSearchMealsLiveData() {
        viewModel.observeSearchedMealListLiveData()
            .observe(viewLifecycleOwner, Observer { listMeals ->
                searchRecyclerViewAdapter.differ.submitList(listMeals)
                if (listMeals != null) {
                    binding.tvNotFound.visibility = View.INVISIBLE
                    binding.rvSearchMeals.visibility = View.VISIBLE
                }

                else{
                    binding.tvNotFound.visibility = View.VISIBLE
                    binding.rvSearchMeals.visibility = View.INVISIBLE
                }
            })
    }

    private fun searchMeals() {
        val searchQuery = binding.edSearchBox.text.toString()
        if (searchQuery.isNotEmpty()) {
            viewModel.searchMeal(searchQuery)
        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerViewAdapter = FavoriteMealsAdapter()
        binding.rvSearchMeals.apply {
            adapter = searchRecyclerViewAdapter
            layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        }
    }
}