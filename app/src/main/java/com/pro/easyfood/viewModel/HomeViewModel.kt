package com.pro.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pro.easyfood.db.MealDatabase
import com.pro.easyfood.pojo.Category
import com.pro.easyfood.pojo.CategoryList
import com.pro.easyfood.pojo.MealsByCategoryList
import com.pro.easyfood.pojo.MealsByCategory
import com.pro.easyfood.pojo.Meal
import com.pro.easyfood.pojo.MealList
import com.pro.easyfood.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val mealDatabase: MealDatabase
) : ViewModel() {
    private var randomMealLiveData = MutableLiveData<Meal>()
    private val popularMealsLiveData = MutableLiveData<List<MealsByCategory>>()
    private val categoriesLiveData = MutableLiveData<List<Category>>()
    private val mealByIdLiveData = MutableLiveData<Meal>()
    private var favoritesMealLiveData = mealDatabase.mealDao().getAllMeals()
    private var searchedMealListViewModel = MutableLiveData<List<Meal>>()
    var saveStateRandomMeal: Meal? = null


    fun searchMeal(name: String) {
        RetrofitInstance.api.searchMeal(name).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    searchedMealListViewModel.value = response.body()!!.meals
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("searchMeal error", t.message.toString())
            }

        })
    }

    fun getMealById(id: String) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    val meal = response.body()!!.meals[0]
                    mealByIdLiveData.value = meal
                }
                else return
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("getMealById error", t.message.toString())
            }

        })
    }

    fun getRandomMeal() {
        saveStateRandomMeal?.let { randomMeal ->
            randomMealLiveData.value = randomMeal
            return
        }
        RetrofitInstance.api.gerRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    val randomMeal: Meal = response.body()!!.meals[0]
                    randomMealLiveData.value = randomMeal
                    saveStateRandomMeal = randomMeal
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("random meal error", t.message.toString())
            }
        })
    }

    fun observeRandomMealLiveData(): LiveData<Meal> {
        return randomMealLiveData
    }

    fun getPopularItems() {
        RetrofitInstance.api.getPopularItems("Beef")
            .enqueue(object : Callback<MealsByCategoryList> {
                override fun onResponse(
                    call: Call<MealsByCategoryList>,
                    response: Response<MealsByCategoryList>
                ) {
                    if (response.body() != null) {
                        popularMealsLiveData.value = response.body()!!.meals
                    }
                }

                override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                    Log.e("popular meals error", t.message.toString())
                }

            })
    }

    fun observePopularLiveData(): LiveData<List<MealsByCategory>> {
        return popularMealsLiveData
    }

    fun getCategories() {
        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList> {
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                if (response.body() != null) {
                    categoriesLiveData.value = response.body()!!.categories
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.e("categories error", t.message.toString())
            }

        })
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealDatabase.mealDao().delete(meal)
        }
    }

    fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().updateMeal(meal)
        }
    }

    fun observeCategoriesLiveData(): LiveData<List<Category>> = categoriesLiveData

    fun observeFavoritesMealsLiveData(): LiveData<List<Meal>> = favoritesMealLiveData

    fun observeMealByIdLiveData(): LiveData<Meal> = mealByIdLiveData

    fun observeSearchedMealListLiveData(): LiveData<List<Meal>> = searchedMealListViewModel
}
