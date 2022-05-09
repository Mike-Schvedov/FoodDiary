package com.mikeschvedov.fooddiary.Data.Database.Daos

import androidx.room.*
import com.mikeschvedov.fooddiary.Data.Database.Entities.FoodEntry
import com.mikeschvedov.fooddiary.Data.Database.Entities.WaterEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {

    @Query("SELECT * FROM water_table ORDER BY waterId ASC")
    fun getAllWaterEntries(): Flow<List<WaterEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWaterEntry(food: WaterEntry)

    @Query("SELECT * FROM water_table WHERE date_added = :targetDate")
    fun getAllWaterWithDate(targetDate: String): Flow<List<WaterEntry>>
}