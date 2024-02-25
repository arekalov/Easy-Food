package com.pro.easyfood.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pro.easyfood.activities.MainActivity
import com.pro.easyfood.activities.MealActivity
import com.pro.easyfood.adapters.FavoriteMealsAdapter
import com.pro.easyfood.databinding.FragmentFavoriteBinding
import com.pro.easyfood.viewModel.HomeViewModel


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var favoriteMealsAdapter: FavoriteMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFavoritesLiveData()
        prepareRecyclerView()
        onItemClickListener()

    }

    private fun onItemClickListener() {
        favoriteMealsAdapter.onCLick = {
            meal->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun prepareRecyclerView() {
        favoriteMealsAdapter = FavoriteMealsAdapter()
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            adapter = favoriteMealsAdapter
        }
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
//                viewModel.deleteMeal(favoriteMealsAdapter.differ.currentList[position])
//                Snackbar.make(requireView(), "Meal deleted", Snackbar.LENGTH_LONG).setAction(
//                    "Undo", View.OnClickListener {
//                        val a = viewModel.insertMeal(favoriteMealsAdapter.differ.currentList[position])
//                    }
//                ).show()
                val position=viewHolder.adapterPosition
                val deletedMeal=favoriteMealsAdapter.differ.currentList[position]
                viewModel.deleteMeal(deletedMeal)
                Snackbar.make(requireView(),"Meal Deleted",Snackbar.LENGTH_LONG).setAction(
                    "Undo",
                    View.OnClickListener {
                        viewModel.insertMeal(deletedMeal)
                    }).show()
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFavorites)
    }

    private fun observeFavoritesLiveData() {
        viewModel.observeFavoritesMealsLiveData().observe(viewLifecycleOwner, Observer {
            meals->
            favoriteMealsAdapter.differ.submitList(meals)
        })
    }

}