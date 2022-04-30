package com.mikeschvedov.fooddiary.Data.Database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Query("SELECT * FROM food_table ORDER BY foodId ASC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(food: FoodEntry)

    @Query("DELETE FROM food_table")
    suspend fun deleteAllEntries()

    @Delete
    suspend fun deleteEntry(food: FoodEntry)
}