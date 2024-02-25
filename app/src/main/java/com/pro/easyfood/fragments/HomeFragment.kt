package com.pro.easyfood.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.bumptech.glide.Glide
import com.pro.easyfood.R
import com.pro.easyfood.activities.CategoryMealsActivity
import com.pro.easyfood.activities.MainActivity
import com.pro.easyfood.activities.MealActivity
import com.pro.easyfood.adapters.CategoryAdapter
import com.pro.easyfood.adapters.MostPopularAdapter
import com.pro.easyfood.databinding.FragmentHomeBinding
import com.pro.easyfood.fragments.bottomsheet.MealBottomSheetFragment
import com.pro.easyfood.pojo.Category
import com.pro.easyfood.pojo.MealsByCategory
import com.pro.easyfood.pojo.Meal
import com.pro.easyfood.viewModel.HomeViewModel

class HomeFragment : Fragment() {
    companion object {
        const val MEAL_ID = "mealId"
        const val MEAL_NAME = "mealName"
        const val MEAL_THUMB = "mealThumb"
        const val CATEGORY_NAME = "categoryName"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeMvvm: HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var popularAdapter: MostPopularAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popularAdapter = MostPopularAdapter()
        categoryAdapter = CategoryAdapter()
        homeMvvm = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeMvvm.getRandomMeal()
        observerRandomMeal()
        onRandomMealClicked()

        preparePopularItemsRecyclerView()
        homeMvvm.getPopularItems()
        observerPopularMeals()
        onPopularMealsClicked()
        onPopularMealLongClick()

        prepareCategoriesRecyclerView()
        homeMvvm.getCategories()
        observerCategories()
        onCategoriesClicked()

        onSearchIconClick()
    }

    private fun onSearchIconClick() {
        binding.ivSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onPopularMealLongClick() {
        popularAdapter.onLongItemClick = {
            meal ->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal info")
        }
    }


    private fun prepareCategoriesRecyclerView() {
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoryAdapter

        }
    }

    private fun preparePopularItemsRecyclerView() {
        binding.recViewMealsPopular.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }
    }

    private fun onPopularMealsClicked() {
        popularAdapter.onItemClick = {
                meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun onCategoriesClicked() {
        categoryAdapter.onItemClick = {
            category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun onRandomMealClicked() {
        binding.randomMealCard.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
            startActivity(intent)
        }
    }


    private fun observerPopularMeals() {
        homeMvvm.observePopularLiveData().observe(viewLifecycleOwner
        ) {mealList ->
            popularAdapter.setMeals(mealList as ArrayList<MealsByCategory>)

        }
    }

    private fun observerRandomMeal() {
        homeMvvm.observeRandomMealLiveData().observe(viewLifecycleOwner
        ) { value ->
            Glide.with(this@HomeFragment)
                .load(value.strMealThumb)
                .into(binding.imgRandomMeal)
            randomMeal = value
        }
    }

    private fun observerCategories() {
        homeMvvm.observeCategoriesLiveData().observe(viewLifecycleOwner) {
                categories ->
            categoryAdapter.setCategoriesList(categories)
        }
    }
}