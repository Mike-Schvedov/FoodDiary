package com.mikeschvedov.fooddiary.Data.Database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    @Query("SELECT * FROM food_table WHERE date_added = :targetDate")
    fun getAllWithDate(targetDate: String): Flow<List<FoodEntry>>
}