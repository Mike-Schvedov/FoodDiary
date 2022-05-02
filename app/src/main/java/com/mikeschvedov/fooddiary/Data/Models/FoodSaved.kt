package com.mikeschvedov.fooddiary.Data.Models

data class FoodSaved(
    var name: String,
    var calPer100gr: Int,
    var image: Int
){
    // Overriding so we can show only the name in the listview
    override fun toString() = this.name
}
