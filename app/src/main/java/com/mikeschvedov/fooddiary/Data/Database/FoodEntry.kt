package com.mikeschvedov.fooddiary.Data.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "food_table")
data class FoodEntry(

    @ColumnInfo(name = "food")
    val foodname: String,
    @ColumnInfo(name = "calories")
    val calories: Int,
    @ColumnInfo(name = "date")
    val date: String
    ){
    @PrimaryKey(autoGenerate = true)
    var foodId: Int = 0 // or foodId: Int? = null
}
