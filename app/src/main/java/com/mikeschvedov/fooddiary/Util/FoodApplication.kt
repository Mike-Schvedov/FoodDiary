package com.mikeschvedov.fooddiary.Util

import android.app.Application
import com.mikeschvedov.fooddiary.Data.Database.FoodDatabase
import com.mikeschvedov.fooddiary.Data.FoodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FoodApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { FoodDatabase.getDatabase(this) }
    val repository by lazy { FoodRepository(database.foodDao()) }
}