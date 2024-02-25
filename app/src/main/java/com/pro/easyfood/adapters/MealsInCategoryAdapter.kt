package com.pro.easyfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pro.easyfood.databinding.MealItemBinding
import com.pro.easyfood.pojo.MealsByCategory

class MealsInCategoryAdapter :
    RecyclerView.Adapter<MealsInCategoryAdapter.MealsInCategoryHolder>() {
    inner class MealsInCategoryHolder(val binding: MealItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    var onItemClick: ((MealsByCategory)->Unit)? = null

    private var mealsInCategory = ArrayList<MealsByCategory>()

    fun setMealsInCategory(list: List<MealsByCategory>) {
        mealsInCategory = list as ArrayList<MealsByCategory>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsInCategoryHolder {
        return MealsInCategoryHolder(
            MealItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mealsInCategory.size
    }

    override fun onBindViewHolder(holder: MealsInCategoryHolder, position: Int) {
        Glide.with(holder.itemView).load(mealsInCategory[position].strMealThumb)
            .into(holder.binding.imgMeal)

        holder.binding.tvMealName.text = mealsInCategory[position].strMeal
        holder.itemView.setOnClickListener{
            onItemClick!!.invoke(mealsInCategory[position])
        }
    }
}