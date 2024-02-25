package com.pro.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pro.easyfood.pojo.MealsByCategory
import com.pro.easyfood.pojo.MealsByCategoryList
import com.pro.easyfood.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryMealsViewModel : ViewModel() {
    val categoryMealsLiveData = MutableLiveData<List<MealsByCategory>>()
    fun getMealsByCategory(category: String) {
        RetrofitInstance.api.getMealsByCategory(category)
            .enqueue(object : Callback<MealsByCategoryList> {
                override fun onResponse(
                    call: Call<MealsByCategoryList>,
                    response: Response<MealsByCategoryList>
                ) {
                    if (response.body() != null) {
                        categoryMealsLiveData.value = response.body()!!.meals
                    }
                }

                override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                    Log.e("meals in categories error", t.message.toString())
                }

            })
    }

    fun observeMealsLiveData(): LiveData<List<MealsByCategory>>{
        return categoryMealsLiveData
    }
}