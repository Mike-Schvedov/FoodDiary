package com.mikeschvedov.fooddiary.Data.Repository

import androidx.annotation.WorkerThread
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Data.Database.FoodEntryDao
import kotlinx.coroutines.flow.Flow
import java.util.*

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FoodRepository(private val foodDao: FoodEntryDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<FoodEntry>> = foodDao.getAllEntries()

    // Unlikw allEntries, now we need to provide a paramenter, so we created a function
    fun getAllEntriesByDate(date: Date): Flow<List<FoodEntry>> {
        return foodDao.getAllWithDate(date)
    }


    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(foodEntry: FoodEntry) {
        foodDao.insertEntry(foodEntry)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(foodEntry: FoodEntry){
        foodDao.deleteEntry(foodEntry)
    }
}