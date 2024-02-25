package com.pro.easyfood.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.pro.easyfood.adapters.MealsInCategoryAdapter
import com.pro.easyfood.databinding.ActivityCategoryMealsBinding
import com.pro.easyfood.fragments.HomeFragment
import com.pro.easyfood.viewModel.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryMealsBinding
    private lateinit var categoryMvvm: CategoryMealsViewModel
    private lateinit var mealsAdapter: MealsInCategoryAdapter
    private lateinit var newIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        categoryMvvm = ViewModelProviders.of(this)[CategoryMealsViewModel::class.java]
        mealsAdapter = MealsInCategoryAdapter()
        setContentView(binding.root)
        newIntent = intent

        prepareRecyclerView()
        categoryMvvm.getMealsByCategory(newIntent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)
        observerCategoryMeals()
        onItemClickListener()
    }

    private fun onItemClickListener() {
        mealsAdapter.onItemClick = {
            meal ->
            val intent = Intent(this, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun prepareRecyclerView() {
        binding.rvMeals.apply {
            adapter = mealsAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun observerCategoryMeals() {
        categoryMvvm.observeMealsLiveData().observe(this){
            categotyList->
            binding.tvCategoryCount.text = "${newIntent.getStringExtra(HomeFragment.CATEGORY_NAME)}: ${categotyList.size}"
            mealsAdapter.setMealsInCategory(categotyList)
        }
    }

}