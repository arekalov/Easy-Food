package com.pro.easyfood.fragments.bottomsheet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pro.easyfood.R
import com.pro.easyfood.activities.MainActivity
import com.pro.easyfood.activities.MealActivity
import com.pro.easyfood.databinding.FragmentMealBottomSheetBinding
import com.pro.easyfood.fragments.HomeFragment
import com.pro.easyfood.pojo.Meal
import com.pro.easyfood.retrofit.RetrofitInstance
import com.pro.easyfood.viewModel.HomeViewModel
import retrofit2.Retrofit


private const val MEAL_ID = "param1"
class MealBottomSheetFragment : BottomSheetDialogFragment() {
    private var mealId: String? = null
    private lateinit var currentMeal: Meal
    private lateinit var binding: FragmentMealBottomSheetBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mealId = it.getString(MEAL_ID)
        }
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMealBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mealId?.let { viewModel.getMealById(it) }
        observeBottomSheetMeal()
        onBottomSheetDialogClick()
    }

    private fun onBottomSheetDialogClick() {
        binding.bottomSheet.setOnClickListener{
            meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, currentMeal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, currentMeal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, currentMeal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observeBottomSheetMeal() {
        viewModel.observeMealByIdLiveData().observe(viewLifecycleOwner, Observer{
            meal ->
            currentMeal = meal
            Glide.with(this).load(meal.strMealThumb).into(binding.imgBottomSheet)
            binding.tvArea.text = "Area: ${meal.strArea}"
            binding.tvCategory.text = "Category: ${meal.strCategory}"
            binding.bottomName.text = meal.strMeal
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MealBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(MEAL_ID, param1)
                }
            }
    }
}