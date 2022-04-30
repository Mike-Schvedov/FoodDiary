package com.mikeschvedov.fooddiary.Data.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Query("SELECT * FROM food_table ORDER BY foodId ASC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(word: FoodEntry)

    @Query("DELETE FROM food_table")
    suspend fun deleteAllEntries()
}