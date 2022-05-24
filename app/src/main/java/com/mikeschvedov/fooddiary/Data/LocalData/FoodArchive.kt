package com.mikeschvedov.fooddiary.Data.LocalData

import com.mikeschvedov.fooddiary.Data.Models.FoodSaved
import com.mikeschvedov.fooddiary.R

class FoodArchive {
    companion object{

        var foodDataList = arrayListOf<FoodSaved>(
            // Vegetables
            FoodSaved("אבוקדו", 160, R.drawable.avocado, false, 0),
            FoodSaved("עגבנייה", 18, R.drawable.tomatoes, false, 0),
            FoodSaved("עגבניות שרי", 18, R.drawable.cherry_tomato, false, 0),
            FoodSaved("מלפפון", 15, R.drawable.cucumber, false, 0),
            FoodSaved("גמבה", 31, R.drawable.bellpeper, false, 0),
            FoodSaved("בצל ירוק", 32, R.drawable.scallion, false, 0),
            FoodSaved("בצל", 32, R.drawable.onion, false, 0),
            FoodSaved("זיתים שחורים (ללא גלעין)", 171, R.drawable.blackolive, false, 0),
            // Dairy
            FoodSaved("חלב", 60, R.drawable.milk, false, 0),
            FoodSaved("מעדן GO", 0, R.drawable.maadango, true, 110),
            FoodSaved("גבינה בולגרית 5%", 140, R.drawable.bulgarian_five, false, 0), //highest 140
            // Fruit
            // Meat & Fish
            FoodSaved(" טונה בשמן", 150, R.drawable.tuna, false, 0),
            FoodSaved(" טונה במים", 100, R.drawable.tuna, false, 0),
            FoodSaved("ביצה", 155, R.drawable.egg, false, 0),
            FoodSaved("בשר כללי (טחון, בקר)", 250, R.drawable.beef, false, 0),
            FoodSaved("סלט טונה", 114, R.drawable.tunasalad, false, 0),
            // Grains and Legumes
            FoodSaved("אורז", 130, R.drawable.rice, false, 0),
            FoodSaved("פתית", 380, R.drawable.patit, false, 0),
            // Brands
            FoodSaved("מיני מק-רויאל (ללא רוטב)", 0, R.drawable.minimc, true, 200),
            FoodSaved("ביג מאק (ללא רוטב)", 0, R.drawable.bigmac, true, 350),
            FoodSaved("תרכיז ויטמינצ\'יק", 352, R.drawable.vitaminchic, false, 0),
            // Other
            FoodSaved("שמן קנולה", 830, R.drawable.kanola, false, 0),


            )


    }
}