package com.mikeschvedov.fooddiary.Data.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "weight_table")
data class WeightEntry(

    @ColumnInfo(name = "weight")
    val weight: Double,
    @ColumnInfo(name = "date_added")
    val date_added: Date,

    ){
    @PrimaryKey(autoGenerate = true)
    var weightId: Int = 0 // or foodId: Int? = null
}
