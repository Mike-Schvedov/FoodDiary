package com.mikeschvedov.fooddiary.Data.LocalData

import com.mikeschvedov.fooddiary.Data.Models.FoodSaved
import com.mikeschvedov.fooddiary.R

class FoodArchive {
    companion object{

        var foodDataList = arrayListOf<FoodSaved>(
            FoodSaved("קפה", 10, R.drawable.ic_baseline_add_24),
            FoodSaved("מנגו", 80, R.drawable.ic_baseline_add_24),
            FoodSaved("אבוקדו", 160, R.drawable.avocado),
            FoodSaved("חלב", 60, R.drawable.ic_baseline_add_24),
            )

    }
}