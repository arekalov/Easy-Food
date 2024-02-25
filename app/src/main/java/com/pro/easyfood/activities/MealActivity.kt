package com.pro.easyfood.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.pro.easyfood.R
import com.pro.easyfood.databinding.ActivityMealBinding
import com.pro.easyfood.db.MealDatabase
import com.pro.easyfood.fragments.HomeFragment
import com.pro.easyfood.pojo.Meal
import com.pro.easyfood.viewModel.MealViewModel
import com.pro.easyfood.viewModel.MealViewModelFactory
import java.net.URL

class MealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealBinding
    private lateinit var mealMvvm: MealViewModel

    private lateinit var mealId:String
    private lateinit var mealName:String
    private lateinit var mealThumb:String
    private lateinit var youtubeLink: String
    private lateinit var mealToSave: Meal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        observerDetailMeal()
        getMealInformationFromIntent()
        setInfoInViews()
        loadingCase()
        mealMvvm.getMealDetail(mealId)
        onYouTubeImageClicked()
        onFavoriteClick()

    }

    private fun onFavoriteClick() {
        binding.fabFavorite.setOnClickListener{
            if (mealToSave != null) {
                mealMvvm.insertMeal(mealToSave)
                Toast.makeText(this@MealActivity, "Meal Saved", Toast.LENGTH_SHORT).show()
            }
            else
            Toast.makeText(this@MealActivity, "Meal saving error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onYouTubeImageClicked() {
        binding.imgYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

    private fun observerDetailMeal() {
        mealMvvm.observeMealDetailLiveData().observe(this, object : Observer<Meal> {
            override fun onChanged(value: Meal) {
                binding.tvCategory.text = "Category: ${value.strCategory}"
                binding.tvArea.text = "Area: ${value.strArea}"
                binding.tvInstr.text = value.strInstructions
                mealToSave = value
                youtubeLink = value.strYoutube.toString()
                onResponseCase()
            }

        })
    }

    private fun setInfoInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)
        binding.collapsingToolbar.title = mealName
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

    private fun loadingCase(){
        binding.progressBar.visibility = View.VISIBLE
        binding.fabFavorite.visibility = View.INVISIBLE
        binding.tvCategory.visibility = View.INVISIBLE
        binding.tvArea.visibility = View.INVISIBLE
        binding.tvInstrTitle.visibility = View.INVISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE

    }

    private fun onResponseCase() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.fabFavorite.visibility = View.VISIBLE
        binding.tvCategory.visibility = View.VISIBLE
        binding.tvArea.visibility = View.VISIBLE
        binding.tvInstrTitle.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE
    }
}