package com.mikeschvedov.fooddiary.Data.Database.Daos

import androidx.room.*
import com.mikeschvedov.fooddiary.Data.Database.Entities.WeightEntry
import kotlinx.coroutines.flow.Flow



@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_table ORDER BY weightId ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeightEntry(weight: WeightEntry)

    @Delete
    suspend fun deleteWeightEntry(weight: WeightEntry)

}