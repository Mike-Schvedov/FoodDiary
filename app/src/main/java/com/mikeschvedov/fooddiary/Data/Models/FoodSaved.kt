package com.mikeschvedov.fooddiary.Data.Models

data class FoodSaved(
    var name: String,
    var calPer100gr: Int,
    var image: Int,
    var asUnit: Boolean,
    var caloriesPerUnit: Int

){
    // Overriding so we can show only the name in the listview
    override fun toString() = this.name
}
