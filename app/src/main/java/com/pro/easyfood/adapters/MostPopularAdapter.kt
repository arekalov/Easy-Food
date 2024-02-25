package com.pro.easyfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pro.easyfood.databinding.PopularItemsBinding
import com.pro.easyfood.pojo.MealsByCategory

class MostPopularAdapter :  RecyclerView.Adapter<MostPopularAdapter.PopularMealViewHolder>(){
    private var  mealList = ArrayList<MealsByCategory>()
    lateinit var onItemClick: ((MealsByCategory) -> Unit)
    var onLongItemClick: ((MealsByCategory) -> Unit)? = null

    fun setMeals(mealList: ArrayList<MealsByCategory>) {
        this.mealList = mealList
        notifyDataSetChanged()

    }
    class PopularMealViewHolder(val binding: PopularItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMealViewHolder {
        return PopularMealViewHolder(PopularItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    override fun onBindViewHolder(holder: PopularMealViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(mealList[position].strMealThumb)
            .into(holder.binding.imgPopoularMealItem)

        holder.itemView.setOnClickListener{
            onItemClick.invoke(mealList[position])
        }

        holder.itemView.setOnLongClickListener{
            onLongItemClick!!.invoke(mealList[position])
            true
        }
    }
}