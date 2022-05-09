package com.mikeschvedov.fooddiary.Data.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_table")
data class WaterEntry(
    @ColumnInfo(name = "waterQuantity")
    val waterQuantity: Int,
    @ColumnInfo(name = "date_added")
    val date: String,
){
    @PrimaryKey(autoGenerate = true)
    var waterId: Int = 0
}
