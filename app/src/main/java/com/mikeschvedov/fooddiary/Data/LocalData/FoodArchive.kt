package com.mikeschvedov.fooddiary.Data.LocalData

import com.mikeschvedov.fooddiary.Data.Models.FoodSaved
import com.mikeschvedov.fooddiary.R

class FoodArchive {
    companion object{

        var foodDataList = arrayListOf<FoodSaved>(
            // Vegetables
            FoodSaved("אבוקדו", 160, R.drawable.avocado, false, 0),
            // Dairy
            FoodSaved("חלב", 60, R.drawable.milk, false, 0),
            // Fruit
            // Meat & Fish
            FoodSaved(" טונה בשמן", 150, R.drawable.tuna, false, 0),
            FoodSaved(" טונה במים", 100, R.drawable.tuna, false, 0),
            FoodSaved("ביצה", 155, R.drawable.egg, false, 0),
            // Grains and Legumes
            FoodSaved("אורז", 130, R.drawable.rice, false, 0),
            // Brands
            FoodSaved("מיני מק-רויאל (ללא רוטב)", 0, R.drawable.minimc, true, 200),
            )

    }
}