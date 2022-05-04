package com.mikeschvedov.fooddiary.Data.LocalData

import com.mikeschvedov.fooddiary.Data.Models.FoodSaved
import com.mikeschvedov.fooddiary.R

class FoodArchive {
    companion object{

        var foodDataList = arrayListOf<FoodSaved>(
            // Vegetables
            FoodSaved("אבוקדו", 160, R.drawable.avocado),
            // Dairy
            FoodSaved("חלב", 60, R.drawable.milk),
            // Fruit
            // Meat & Fish
            FoodSaved(" טונה בשמן", 150, R.drawable.tuna),
            FoodSaved(" טונה במים", 100, R.drawable.tuna),
            FoodSaved("ביצה", 155, R.drawable.egg),
            // Grains and Legumes
            FoodSaved("אורז", 130, R.drawable.rice),
            )

    }
}